package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DefaultStudioSeed
import com.example.data.PreferencesManager
import com.example.data.StudioDataSource
import com.example.data.model.BusinessConfig
import com.example.data.model.CartItem
import com.example.data.model.CustomerProfile
import com.example.data.model.OrderStatus
import com.example.data.model.ServiceCategory
import com.example.data.model.StudioOrder
import com.example.data.model.StudioReview
import com.example.data.model.StudioService
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface StudioEvent {
    data class ShowMessage(val message: String) : StudioEvent
    data class LaunchWhatsAppOrder(val phoneNumber: String, val message: String) : StudioEvent
}

class StudioViewModel(
    private val repository: StudioDataSource,
    private val preferences: PreferencesManager,
    val currentUserId: String,
    val currentUserName: String,
    val currentUserEmail: String
) : ViewModel() {

    private val _events = MutableSharedFlow<StudioEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<StudioEvent> = _events.asSharedFlow()

    private val _isAdminUser = MutableStateFlow(false)
    val isAdminUser: StateFlow<Boolean> = _isAdminUser.asStateFlow()

    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ServiceCategory?>(null)
    val selectedCategory: StateFlow<ServiceCategory?> = _selectedCategory.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _isActionLoading = MutableStateFlow(false)
    val isActionLoading: StateFlow<Boolean> = _isActionLoading.asStateFlow()

    val hindiFriendlyMode: StateFlow<Boolean> = preferences.hindiFriendlyMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), true)

    val adminPasscode: StateFlow<String> = preferences.adminPasscode
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            DefaultStudioSeed.DEFAULT_ADMIN_PASSCODE
        )

    val servicesState: StateFlow<UiState<List<StudioService>>> = repository.observeServices()
        .map<List<StudioService>, UiState<List<StudioService>>> { UiState.Success(it) }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val root = firestoreError ?: error
            Log.w(TAG, "Error observing services", root)
            emit(UiState.Error(root.message ?: "Unable to load studio services"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val filteredServices: StateFlow<List<StudioService>> = combine(
        servicesState,
        _searchQuery,
        _selectedCategory
    ) { state, query, category ->
        val all = (state as? UiState.Success)?.data ?: emptyList()
        val q = query.trim().lowercase()
        all.filter { service ->
            val matchesCat = category == null || service.categoryEnum == category
            val matchesQuery = q.isBlank() ||
                service.name.lowercase().contains(q) ||
                service.nameHindi.lowercase().contains(q) ||
                service.description.lowercase().contains(q)
            matchesCat && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val businessConfig: StateFlow<BusinessConfig> = repository.observeBusinessConfig()
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            Log.w(TAG, "Error observing business config", firestoreError ?: error)
            emit(DefaultStudioSeed.defaultBusinessConfig)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            DefaultStudioSeed.defaultBusinessConfig
        )

    val myOrdersState: StateFlow<UiState<List<StudioOrder>>> = repository.observeMyOrders(currentUserId)
        .map<List<StudioOrder>, UiState<List<StudioOrder>>> { UiState.Success(it) }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val root = firestoreError ?: error
            Log.w(TAG, "Error observing my orders", root)
            emit(UiState.Error(root.message ?: "Unable to load your orders"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val reviewsState: StateFlow<UiState<List<StudioReview>>> = repository.observeReviews()
        .map<List<StudioReview>, UiState<List<StudioReview>>> { UiState.Success(it) }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val root = firestoreError ?: error
            Log.w(TAG, "Error observing reviews", root)
            emit(UiState.Error(root.message ?: "Unable to load reviews"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    private val _adminOrdersState = MutableStateFlow<UiState<List<StudioOrder>>>(UiState.Loading)
    val adminOrdersState: StateFlow<UiState<List<StudioOrder>>> = _adminOrdersState.asStateFlow()

    private val _adminCustomersState = MutableStateFlow<UiState<List<CustomerProfile>>>(UiState.Loading)
    val adminCustomersState: StateFlow<UiState<List<CustomerProfile>>> = _adminCustomersState.asStateFlow()

    private var adminObserversStarted = false

    init {
        viewModelScope.launch {
            val isAdmin = repository.checkIsAdmin()
            _isAdminUser.value = isAdmin
            if (isAdmin) {
                repository.seedStarterServicesIfEmpty()
                startAdminStreams()
            }
        }
    }

    private fun startAdminStreams() {
        if (adminObserversStarted) return
        adminObserversStarted = true

        viewModelScope.launch {
            repository.observeAllOrdersAdmin()
                .map<List<StudioOrder>, UiState<List<StudioOrder>>> { UiState.Success(it) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    emit(UiState.Error((fsErr ?: error).message ?: "Failed to load orders"))
                }
                .collect { _adminOrdersState.value = it }
        }

        viewModelScope.launch {
            repository.observeCustomersAdmin()
                .map<List<CustomerProfile>, UiState<List<CustomerProfile>>> { UiState.Success(it) }
                .catch { error ->
                    val fsErr = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsErr == null && error is CancellationException) throw error
                    emit(UiState.Error((fsErr ?: error).message ?: "Failed to load customers"))
                }
                .collect { _adminCustomersState.value = it }
        }
    }

    fun toggleHindiMode() {
        viewModelScope.launch {
            preferences.setHindiFriendlyMode(!hindiFriendlyMode.value)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: ServiceCategory?) {
        _selectedCategory.value = category
    }

    // --- CART MANAGEMENT ---
    fun addToCart(service: StudioService, quantity: Int = 1) {
        val addQty = quantity.coerceIn(1, 500)
        _cartItems.update { list ->
            val existing = list.find { it.service.id == service.id && it.service.name == service.name }
            if (existing != null) {
                list.map {
                    if (it.service.id == service.id && it.service.name == service.name) {
                        it.copy(quantity = (it.quantity + addQty).coerceAtMost(500))
                    } else it
                }
            } else {
                list + CartItem(service = service, quantity = addQty)
            }
        }
        _events.tryEmit(StudioEvent.ShowMessage("${service.name} added to cart"))
    }

    fun updateCartQuantity(serviceId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(serviceId)
            return
        }
        _cartItems.update { list ->
            list.map {
                if (it.service.id == serviceId) it.copy(quantity = newQuantity.coerceAtMost(500)) else it
            }
        }
    }

    fun removeFromCart(serviceId: String) {
        _cartItems.update { list -> list.filterNot { it.service.id == serviceId } }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // --- ORDER PLACEMENT & WHATSAPP ---
    fun placeOrderAndLaunchWhatsApp(
        customerName: String,
        customerPhone: String,
        requirements: String,
        items: List<CartItem>,
        clearCartOnSuccess: Boolean,
        buildMessage: (orderId: String, items: List<CartItem>) -> String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isActionLoading.value = true
            val result = repository.placeOrder(
                customerName = customerName,
                customerPhone = customerPhone,
                requirements = requirements,
                cartItems = items
            )
            _isActionLoading.value = false
            result.onSuccess { savedOrder ->
                if (clearCartOnSuccess) {
                    clearCart()
                }
                val msg = buildMessage(savedOrder.id, items)
                _events.tryEmit(
                    StudioEvent.LaunchWhatsAppOrder(
                        phoneNumber = businessConfig.value.whatsappNumber,
                        message = msg
                    )
                )
                _events.tryEmit(StudioEvent.ShowMessage("Order #${savedOrder.id} saved! Opening WhatsApp..."))
                onComplete()
            }.onFailure { err ->
                _events.tryEmit(StudioEvent.ShowMessage(err.localizedMessage ?: "Failed to place order"))
            }
        }
    }

    // --- REVIEWS ---
    fun submitReview(authorName: String, rating: Int, serviceUsed: String, comment: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isActionLoading.value = true
            val res = repository.submitReview(authorName, rating, serviceUsed, comment)
            _isActionLoading.value = false
            res.onSuccess {
                _events.tryEmit(StudioEvent.ShowMessage("Thank you for reviewing Studio Pixy!"))
                onSuccess()
            }.onFailure { err ->
                _events.tryEmit(StudioEvent.ShowMessage(err.localizedMessage ?: "Unable to submit review"))
            }
        }
    }

    // --- ADMIN GATE & ACTIONS ---
    fun verifyAdminPasscode(input: String): Boolean {
        val matches = input.trim() == adminPasscode.value
        if (matches) {
            _isAdminUnlocked.value = true
            if (_isAdminUser.value) {
                startAdminStreams()
            }
        }
        return matches
    }

    fun lockAdminPanel() {
        _isAdminUnlocked.value = false
    }

    fun updateAdminPasscode(newPasscode: String) {
        viewModelScope.launch {
            if (newPasscode.trim().length < 4) {
                _events.tryEmit(StudioEvent.ShowMessage("Passcode must be at least 4 characters"))
                return@launch
            }
            preferences.setAdminPasscode(newPasscode)
            _events.tryEmit(StudioEvent.ShowMessage("Admin passcode updated"))
        }
    }

    fun restoreStarterCatalog() {
        viewModelScope.launch {
            _isActionLoading.value = true
            val res = repository.forceRestoreStarterServices()
            _isActionLoading.value = false
            res.onSuccess { count ->
                _events.tryEmit(StudioEvent.ShowMessage("Initialized $count studio services in Firestore"))
            }.onFailure { err ->
                _events.tryEmit(StudioEvent.ShowMessage(err.localizedMessage ?: "Admin access required to seed catalog"))
            }
        }
    }

    fun saveService(service: StudioService, onDone: () -> Unit) {
        viewModelScope.launch {
            _isActionLoading.value = true
            val res = repository.saveService(service)
            _isActionLoading.value = false
            res.onSuccess {
                _events.tryEmit(StudioEvent.ShowMessage("Saved '${service.name}'"))
                onDone()
            }.onFailure { err ->
                _events.tryEmit(StudioEvent.ShowMessage(err.localizedMessage ?: "Failed to save service"))
            }
        }
    }

    fun deleteService(service: StudioService) {
        viewModelScope.launch {
            _isActionLoading.value = true
            val res = repository.deleteService(service.id)
            _isActionLoading.value = false
            res.onSuccess {
                _events.tryEmit(StudioEvent.ShowMessage("Deleted '${service.name}'"))
            }.onFailure { err ->
                _events.tryEmit(StudioEvent.ShowMessage(err.localizedMessage ?: "Failed to delete service"))
            }
        }
    }

    fun saveBusinessConfig(config: BusinessConfig) {
        viewModelScope.launch {
            _isActionLoading.value = true
            val res = repository.saveBusinessConfig(config)
            _isActionLoading.value = false
            res.onSuccess {
                _events.tryEmit(StudioEvent.ShowMessage("Studio contact details updated!"))
            }.onFailure { err ->
                _events.tryEmit(StudioEvent.ShowMessage(err.localizedMessage ?: "Failed to update business settings"))
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus, adminNotes: String) {
        viewModelScope.launch {
            _isActionLoading.value = true
            val res = repository.updateOrderStatus(orderId, status, adminNotes)
            _isActionLoading.value = false
            res.onSuccess {
                _events.tryEmit(StudioEvent.ShowMessage("Order #$orderId marked ${status.displayName}"))
            }.onFailure { err ->
                _events.tryEmit(StudioEvent.ShowMessage(err.localizedMessage ?: "Failed to update order"))
            }
        }
    }

    fun updateCustomer(customerId: String, displayName: String, phone: String, notes: String, onDone: () -> Unit) {
        viewModelScope.launch {
            _isActionLoading.value = true
            val res = repository.updateCustomerAdmin(customerId, displayName, phone, notes)
            _isActionLoading.value = false
            res.onSuccess {
                _events.tryEmit(StudioEvent.ShowMessage("Customer details updated"))
                onDone()
            }.onFailure { err ->
                _events.tryEmit(StudioEvent.ShowMessage(err.localizedMessage ?: "Failed to update customer"))
            }
        }
    }

    private companion object {
        const val TAG = "StudioViewModel"
    }
}
