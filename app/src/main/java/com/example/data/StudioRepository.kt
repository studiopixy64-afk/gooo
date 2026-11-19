package com.example.data

import android.content.Context
import com.example.R
import com.example.data.model.BusinessConfig
import com.example.data.model.CartItem
import com.example.data.model.CustomerProfile
import com.example.data.model.OrderLineItem
import com.example.data.model.OrderStatus
import com.example.data.model.StudioOrder
import com.example.data.model.StudioReview
import com.example.data.model.StudioService
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StudioRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) : StudioDataSource {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Please sign in with Google to continue.")
    }

    override suspend fun checkIsAdmin(): Boolean {
        val user = auth.currentUser ?: return false
        if (user.email.equals(DefaultStudioSeed.ADMIN_EMAIL, ignoreCase = true) && user.isEmailVerified) {
            return true
        }
        return try {
            val snap = db.collection("admins").document(user.uid).get().await()
            snap.exists()
        } catch (_: Exception) {
            false
        }
    }

    // --- SERVICES CATALOG ---
    override fun observeServices(): Flow<List<StudioService>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("services")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        StudioService::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedBy { it.sortOrder }
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == uid) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, "services")
                    }
                    throw error
                }
        )
    }

    override suspend fun seedStarterServicesIfEmpty(): Result<Int> {
        val path = "services"
        return try {
            requireUserId()
            val existing = db.collection(path).limit(1).get().await()
            if (!existing.isEmpty) return Result.success(0)

            val batch = db.batch()
            val seedList = DefaultStudioSeed.starterServices()
            seedList.forEach { (docId, service) ->
                val ref = db.collection(path).document(docId)
                batch.set(ref, service.toCreateMap())
            }
            val configRef = db.collection("business_config").document("studio_profile")
            val configSnap = configRef.get().await()
            if (!configSnap.exists()) {
                batch.set(configRef, DefaultStudioSeed.defaultBusinessConfig.toCreateMap())
            }
            batch.commit().await()
            Result.success(seedList.size)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    override suspend fun forceRestoreStarterServices(): Result<Int> {
        val path = "services"
        return try {
            requireUserId()
            val batch = db.batch()
            val seedList = DefaultStudioSeed.starterServices()
            seedList.forEach { (docId, service) ->
                val ref = db.collection(path).document(docId)
                batch.set(ref, service.toCreateMap())
            }
            batch.commit().await()
            Result.success(seedList.size)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    override suspend fun saveService(service: StudioService): Result<Unit> {
        val isCreate = service.id.isBlank()
        val docId = if (isCreate) "svc_${UUID.randomUUID().toString().replace("-", "").take(16)}" else service.id
        val docPath = "services/$docId"
        return try {
            requireUserId()
            require(service.name.isNotBlank()) { "Service name cannot be empty" }
            require(service.priceInr >= 0L) { "Price cannot be negative" }
            val ref = db.collection("services").document(docId)
            if (isCreate) {
                ref.set(service.toCreateMap()).await()
            } else {
                ref.update(service.toUpdateMap()).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, if (isCreate) OperationType.CREATE else OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun deleteService(serviceId: String): Result<Unit> {
        val docPath = "services/$serviceId"
        return try {
            requireUserId()
            require(serviceId.isNotBlank()) { "Service ID required" }
            db.collection("services").document(serviceId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    // --- BUSINESS CONFIG ---
    override fun observeBusinessConfig(): Flow<BusinessConfig> = flow {
        val uid = requireUserId()
        val docPath = "business_config/studio_profile"
        emitAll(
            db.collection("business_config").document("studio_profile")
                .snapshots()
                .map { snap ->
                    if (snap.exists()) {
                        snap.toObject(
                            BusinessConfig::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ) ?: DefaultStudioSeed.defaultBusinessConfig
                    } else {
                        DefaultStudioSeed.defaultBusinessConfig
                    }
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == uid) {
                        handleFirestoreError(fsErr ?: error, OperationType.GET, docPath)
                    }
                    throw error
                }
        )
    }

    override suspend fun saveBusinessConfig(config: BusinessConfig): Result<Unit> {
        val docPath = "business_config/studio_profile"
        return try {
            requireUserId()
            require(config.businessName.isNotBlank()) { "Business name is required" }
            require(config.whatsappNumber.trim().length >= 7) { "Valid WhatsApp number is required" }
            require(config.phoneNumber.trim().length >= 7) { "Valid phone number is required" }
            val ref = db.collection("business_config").document("studio_profile")
            val snap = ref.get().await()
            if (snap.exists()) {
                ref.update(config.toUpdateMap()).await()
            } else {
                ref.set(config.toCreateMap()).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    // --- ORDERS ---
    override fun observeMyOrders(userId: String): Flow<List<StudioOrder>> = flow {
        val uid = requireUserId()
        check(uid == userId)
        emitAll(
            db.collection("orders")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        StudioOrder::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == uid) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, "orders")
                    }
                    throw error
                }
        )
    }

    override fun observeAllOrdersAdmin(): Flow<List<StudioOrder>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("orders")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        StudioOrder::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == uid) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, "orders")
                    }
                    throw error
                }
        )
    }

    override suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        requirements: String,
        cartItems: List<CartItem>
    ): Result<StudioOrder> {
        val orderId = "ord_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        val docPath = "orders/$orderId"
        return try {
            val uid = requireUserId()
            val email = auth.currentUser?.email ?: ""
            require(customerName.trim().isNotEmpty()) { "Please enter your name" }
            require(customerPhone.trim().length >= 7) { "Please enter a valid phone number" }
            require(cartItems.isNotEmpty()) { "Select at least one service" }

            val lineItems = cartItems.take(30).map { cartItem ->
                OrderLineItem(
                    serviceId = cartItem.service.id,
                    serviceName = cartItem.service.name,
                    unitPriceInr = cartItem.service.priceInr,
                    quantity = cartItem.quantity.toLong().coerceIn(1L, 500L),
                    lineTotalInr = cartItem.lineTotalInr
                )
            }
            val totalQty = lineItems.sumOf { it.quantity }.coerceIn(1L, 1000L)
            val totalAmt = lineItems.sumOf { it.lineTotalInr }.coerceIn(0L, 50_000_000L)

            val payload = mapOf(
                "userId" to uid,
                "customerEmail" to email.take(120),
                "customerName" to customerName.trim().take(80),
                "customerPhone" to customerPhone.trim().take(20),
                "requirements" to requirements.trim().take(600),
                "items" to lineItems.map { it.toMap() },
                "totalQuantity" to totalQty,
                "totalAmountInr" to totalAmt,
                "status" to OrderStatus.PENDING.name,
                "adminNotes" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("orders").document(orderId).set(payload).await()

            // Upsert customer profile cleanly for Admin Customer Management
            upsertCustomerOnOrder(
                uid = uid,
                displayName = customerName.trim().take(80),
                email = email.take(120),
                phone = customerPhone.trim().take(20)
            )

            Result.success(
                StudioOrder(
                    id = orderId,
                    userId = uid,
                    customerEmail = email,
                    customerName = customerName.trim(),
                    customerPhone = customerPhone.trim(),
                    requirements = requirements.trim(),
                    items = lineItems,
                    totalQuantity = totalQty,
                    totalAmountInr = totalAmt,
                    status = OrderStatus.PENDING.name,
                    createdAt = Timestamp.now()
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus,
        adminNotes: String
    ): Result<Unit> {
        val docPath = "orders/$orderId"
        return try {
            requireUserId()
            require(orderId.isNotBlank()) { "Order ID required" }
            db.collection("orders").document(orderId).update(
                mapOf(
                    "status" to newStatus.name,
                    "adminNotes" to adminNotes.trim().take(400),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    // --- CUSTOMERS ---
    private suspend fun upsertCustomerOnOrder(
        uid: String,
        displayName: String,
        email: String,
        phone: String
    ) {
        val userRef = db.collection("users").document(uid)
        try {
            val snap = userRef.get().await()
            if (snap.exists()) {
                val currentCount = snap.getLong("orderCount") ?: 0L
                userRef.update(
                    mapOf(
                        "displayName" to displayName,
                        "email" to email,
                        "phone" to phone,
                        "orderCount" to (currentCount + 1L),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                userRef.set(
                    mapOf(
                        "userId" to uid,
                        "displayName" to displayName,
                        "email" to email,
                        "phone" to phone,
                        "notes" to "",
                        "orderCount" to 1L,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            handleFirestoreError(e, OperationType.WRITE, "users/$uid")
        }
    }

    override fun observeCustomersAdmin(): Flow<List<CustomerProfile>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CustomerProfile::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.updatedAt ?: it.createdAt ?: Timestamp(0, 0) }
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == uid) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, "users")
                    }
                    throw error
                }
        )
    }

    override suspend fun updateCustomerAdmin(
        customerId: String,
        displayName: String,
        phone: String,
        notes: String
    ): Result<Unit> {
        val docPath = "users/$customerId"
        return try {
            requireUserId()
            require(customerId.isNotBlank()) { "Customer ID required" }
            require(displayName.isNotBlank()) { "Customer name required" }
            db.collection("users").document(customerId).update(
                mapOf(
                    "displayName" to displayName.trim().take(80),
                    "phone" to phone.trim().take(20),
                    "notes" to notes.trim().take(400),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    // --- REVIEWS ---
    override fun observeReviews(): Flow<List<StudioReview>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("reviews")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        StudioReview::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == uid) {
                        handleFirestoreError(fsErr ?: error, OperationType.LIST, "reviews")
                    }
                    throw error
                }
        )
    }

    override suspend fun submitReview(
        authorName: String,
        rating: Int,
        serviceUsed: String,
        comment: String
    ): Result<Unit> {
        val reviewId = "rev_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        val docPath = "reviews/$reviewId"
        return try {
            val uid = requireUserId()
            require(authorName.trim().isNotEmpty()) { "Name is required" }
            require(comment.trim().length >= 2) { "Please write a brief review" }
            val payload = mapOf(
                "userId" to uid,
                "authorName" to authorName.trim().take(60),
                "rating" to rating.coerceIn(1, 5).toLong(),
                "serviceUsed" to serviceUsed.trim().ifBlank { "Studio Pixy" }.take(80),
                "comment" to comment.trim().take(400),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("reviews").document(reviewId).set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }
}
