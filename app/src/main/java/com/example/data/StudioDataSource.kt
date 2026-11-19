package com.example.data

import com.example.data.model.BusinessConfig
import com.example.data.model.CartItem
import com.example.data.model.CustomerProfile
import com.example.data.model.OrderLineItem
import com.example.data.model.OrderStatus
import com.example.data.model.StudioOrder
import com.example.data.model.StudioReview
import com.example.data.model.StudioService
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

interface StudioDataSource {
    suspend fun checkIsAdmin(): Boolean
    fun observeServices(): Flow<List<StudioService>>
    suspend fun seedStarterServicesIfEmpty(): Result<Int>
    suspend fun forceRestoreStarterServices(): Result<Int>
    suspend fun saveService(service: StudioService): Result<Unit>
    suspend fun deleteService(serviceId: String): Result<Unit>
    fun observeBusinessConfig(): Flow<BusinessConfig>
    suspend fun saveBusinessConfig(config: BusinessConfig): Result<Unit>
    fun observeMyOrders(userId: String): Flow<List<StudioOrder>>
    fun observeAllOrdersAdmin(): Flow<List<StudioOrder>>
    suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        requirements: String,
        cartItems: List<CartItem>
    ): Result<StudioOrder>

    suspend fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus,
        adminNotes: String
    ): Result<Unit>

    fun observeCustomersAdmin(): Flow<List<CustomerProfile>>
    suspend fun updateCustomerAdmin(
        customerId: String,
        displayName: String,
        phone: String,
        notes: String
    ): Result<Unit>

    fun observeReviews(): Flow<List<StudioReview>>
    suspend fun submitReview(
        authorName: String,
        rating: Int,
        serviceUsed: String,
        comment: String
    ): Result<Unit>
}

/**
 * Device-local fallback repository used when Firebase is not configured in the APK
 * (for example, builds created without app/google-services.json), ensuring the app
 * opens cleanly with the complete Studio Pixy catalog and WhatsApp ordering flow.
 */
class LocalStudioRepository : StudioDataSource {

    private val servicesFlow = MutableStateFlow(
        DefaultStudioSeed.starterServices().map { (id, service) -> service.copy(id = id) }
    )
    private val configFlow = MutableStateFlow(DefaultStudioSeed.defaultBusinessConfig)
    private val ordersFlow = MutableStateFlow<List<StudioOrder>>(emptyList())
    private val customersFlow = MutableStateFlow<List<CustomerProfile>>(emptyList())
    private val reviewsFlow = MutableStateFlow<List<StudioReview>>(emptyList())

    override suspend fun checkIsAdmin(): Boolean = true

    override fun observeServices(): Flow<List<StudioService>> =
        servicesFlow.map { list -> list.sortedBy { it.sortOrder } }

    override suspend fun seedStarterServicesIfEmpty(): Result<Int> {
        if (servicesFlow.value.isNotEmpty()) return Result.success(0)
        val seeded = DefaultStudioSeed.starterServices().map { (id, service) -> service.copy(id = id) }
        servicesFlow.value = seeded
        return Result.success(seeded.size)
    }

    override suspend fun forceRestoreStarterServices(): Result<Int> {
        val seeded = DefaultStudioSeed.starterServices().map { (id, service) -> service.copy(id = id) }
        servicesFlow.update { existing ->
            val seededIds = seeded.map { it.id }.toSet()
            existing.filterNot { it.id in seededIds } + seeded
        }
        return Result.success(seeded.size)
    }

    override suspend fun saveService(service: StudioService): Result<Unit> {
        if (service.name.isBlank()) return Result.failure(IllegalArgumentException("Service name cannot be empty"))
        if (service.priceInr < 0L) return Result.failure(IllegalArgumentException("Price cannot be negative"))
        val docId = if (service.id.isBlank()) {
            "svc_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        } else {
            service.id
        }
        val saved = service.copy(id = docId, updatedAt = Timestamp.now())
        servicesFlow.update { list ->
            if (list.any { it.id == docId }) {
                list.map { if (it.id == docId) saved else it }
            } else {
                list + saved
            }
        }
        return Result.success(Unit)
    }

    override suspend fun deleteService(serviceId: String): Result<Unit> {
        if (serviceId.isBlank()) return Result.failure(IllegalArgumentException("Service ID required"))
        servicesFlow.update { list -> list.filterNot { it.id == serviceId } }
        return Result.success(Unit)
    }

    override fun observeBusinessConfig(): Flow<BusinessConfig> = configFlow.asStateFlow()

    override suspend fun saveBusinessConfig(config: BusinessConfig): Result<Unit> {
        if (config.businessName.isBlank()) return Result.failure(IllegalArgumentException("Business name is required"))
        if (config.whatsappNumber.trim().length < 7) return Result.failure(IllegalArgumentException("Valid WhatsApp number is required"))
        if (config.phoneNumber.trim().length < 7) return Result.failure(IllegalArgumentException("Valid phone number is required"))
        configFlow.value = config.copy(updatedAt = Timestamp.now())
        return Result.success(Unit)
    }

    override fun observeMyOrders(userId: String): Flow<List<StudioOrder>> =
        ordersFlow.map { list -> list.filter { it.userId == userId } }

    override fun observeAllOrdersAdmin(): Flow<List<StudioOrder>> = ordersFlow.asStateFlow()

    override suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        requirements: String,
        cartItems: List<CartItem>
    ): Result<StudioOrder> {
        if (customerName.trim().isEmpty()) return Result.failure(IllegalArgumentException("Please enter your name"))
        if (customerPhone.trim().length < 7) return Result.failure(IllegalArgumentException("Please enter a valid phone number"))
        if (cartItems.isEmpty()) return Result.failure(IllegalArgumentException("Select at least one service"))

        val orderId = "ord_${UUID.randomUUID().toString().replace("-", "").take(14)}"
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
        val now = Timestamp.now()

        val savedOrder = StudioOrder(
            id = orderId,
            userId = "local_guest",
            customerEmail = "",
            customerName = customerName.trim().take(80),
            customerPhone = customerPhone.trim().take(20),
            requirements = requirements.trim().take(600),
            items = lineItems,
            totalQuantity = totalQty,
            totalAmountInr = totalAmt,
            status = OrderStatus.PENDING.name,
            createdAt = now,
            updatedAt = now
        )
        ordersFlow.update { listOf(savedOrder) + it }

        customersFlow.update { list ->
            val existing = list.find { it.phone == savedOrder.customerPhone }
            if (existing != null) {
                list.map {
                    if (it.id == existing.id) {
                        it.copy(
                            displayName = savedOrder.customerName,
                            orderCount = it.orderCount + 1L,
                            updatedAt = now
                        )
                    } else it
                }
            } else {
                listOf(
                    CustomerProfile(
                        id = "cust_${UUID.randomUUID().toString().replace("-", "").take(10)}",
                        userId = "local_guest",
                        displayName = savedOrder.customerName,
                        phone = savedOrder.customerPhone,
                        orderCount = 1L,
                        createdAt = now,
                        updatedAt = now
                    )
                ) + list
            }
        }
        return Result.success(savedOrder)
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus,
        adminNotes: String
    ): Result<Unit> {
        if (orderId.isBlank()) return Result.failure(IllegalArgumentException("Order ID required"))
        ordersFlow.update { list ->
            list.map {
                if (it.id == orderId) {
                    it.copy(
                        status = newStatus.name,
                        adminNotes = adminNotes.trim().take(400),
                        updatedAt = Timestamp.now()
                    )
                } else it
            }
        }
        return Result.success(Unit)
    }

    override fun observeCustomersAdmin(): Flow<List<CustomerProfile>> = customersFlow.asStateFlow()

    override suspend fun updateCustomerAdmin(
        customerId: String,
        displayName: String,
        phone: String,
        notes: String
    ): Result<Unit> {
        if (customerId.isBlank()) return Result.failure(IllegalArgumentException("Customer ID required"))
        if (displayName.isBlank()) return Result.failure(IllegalArgumentException("Customer name required"))
        customersFlow.update { list ->
            list.map {
                if (it.id == customerId) {
                    it.copy(
                        displayName = displayName.trim().take(80),
                        phone = phone.trim().take(20),
                        notes = notes.trim().take(400),
                        updatedAt = Timestamp.now()
                    )
                } else it
            }
        }
        return Result.success(Unit)
    }

    override fun observeReviews(): Flow<List<StudioReview>> = reviewsFlow.asStateFlow()

    override suspend fun submitReview(
        authorName: String,
        rating: Int,
        serviceUsed: String,
        comment: String
    ): Result<Unit> {
        if (authorName.trim().isEmpty()) return Result.failure(IllegalArgumentException("Name is required"))
        if (comment.trim().length < 2) return Result.failure(IllegalArgumentException("Please write a brief review"))
        val now = Timestamp.now()
        val review = StudioReview(
            id = "rev_${UUID.randomUUID().toString().replace("-", "").take(14)}",
            userId = "local_guest",
            authorName = authorName.trim().take(60),
            rating = rating.coerceIn(1, 5).toLong(),
            serviceUsed = serviceUsed.trim().ifBlank { "Studio Pixy" }.take(80),
            comment = comment.trim().take(400),
            createdAt = now,
            updatedAt = now
        )
        reviewsFlow.update { listOf(review) + it }
        return Result.success(Unit)
    }
}
