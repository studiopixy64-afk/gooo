package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.LocalStudioRepository
import com.example.data.PreferencesManager
import com.example.data.StudioRepository
import com.example.data.model.CartItem
import com.example.data.model.StudioService
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.authStateFlow
import com.example.ui.auth.safeFirebaseAuth
import com.example.ui.auth.signOutUser
import com.example.ui.components.ServiceOrderBottomSheet
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ServicesScreen
import com.example.ui.screens.StudioInfoScreen
import com.example.ui.theme.AtelierCharcoal
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.IvoryCream
import com.example.ui.theme.MutedSilver
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateVelvet
import com.example.ui.theme.StudioPixyTheme
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.StudioEvent
import com.example.ui.viewmodel.StudioViewModel
import com.example.util.IntentHelpers
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

object StudioRoutes {
    const val ROUTE_HOME = "home"
    const val ROUTE_SERVICES = "services"
    const val ROUTE_ORDERS = "orders"
    const val ROUTE_STUDIO = "studio"
    const val ROUTE_ADMIN = "admin"
}

private data class BottomTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudioPixyTheme {
                StudioPixyAppRoot()
            }
        }
    }
}

@Composable
fun StudioPixyAppRoot(auth: FirebaseAuth? = null) {
    val context = LocalContext.current
    val firebaseAuth = remember(auth, context) { auth ?: safeFirebaseAuth(context) }
    var showSplashOverlay by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(900L)
        showSplashOverlay = false
    }

    Box(modifier = Modifier.fillMaxSize().background(ObsidianBlack)) {
        if (firebaseAuth == null) {
            AuthenticatedStudioApp(
                useCloudBackend = false,
                currentUserId = "local_guest",
                currentUserName = "Guest Customer",
                currentUserEmail = ""
            )
        } else {
            val currentUser by firebaseAuth.authStateFlow()
                .collectAsStateWithLifecycle(initialValue = firebaseAuth.currentUser)
            val user = currentUser
            if (user == null) {
                AuthScreen(onAuthSuccess = {})
            } else {
                key(user.uid) {
                    AuthenticatedStudioApp(
                        useCloudBackend = true,
                        currentUserId = user.uid,
                        currentUserName = user.displayName ?: "Customer",
                        currentUserEmail = user.email ?: ""
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showSplashOverlay,
            exit = fadeOut(animationSpec = tween(350))
        ) {
            StudioSplashScreen()
        }
    }
}

@Composable
private fun StudioSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = ObsidianBlack,
                border = BorderStroke(2.dp, ChampagneGold),
                shadowElevation = 20.dp,
                modifier = Modifier.size(116.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_studio_logo),
                    contentDescription = "Studio Pixy Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "STUDIO PIXY",
                style = MaterialTheme.typography.displayLarge,
                color = WarmGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Photography • Frames • Prints",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedSilver
            )
        }
    }
}

@Composable
private fun AuthenticatedStudioApp(
    useCloudBackend: Boolean,
    currentUserId: String,
    currentUserName: String,
    currentUserEmail: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val viewModel: StudioViewModel = viewModel(
        key = "${useCloudBackend}_$currentUserId",
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val dataSource = if (useCloudBackend) {
                    StudioRepository(app)
                } else {
                    LocalStudioRepository()
                }
                StudioViewModel(
                    repository = dataSource,
                    preferences = PreferencesManager(app),
                    currentUserId = currentUserId,
                    currentUserName = currentUserName,
                    currentUserEmail = currentUserEmail
                )
            }
        }
    )

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: StudioRoutes.ROUTE_HOME

    val servicesState by viewModel.servicesState.collectAsStateWithLifecycle()
    val filteredServices by viewModel.filteredServices.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val businessConfig by viewModel.businessConfig.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val myOrdersState by viewModel.myOrdersState.collectAsStateWithLifecycle()
    val reviewsState by viewModel.reviewsState.collectAsStateWithLifecycle()
    val adminOrdersState by viewModel.adminOrdersState.collectAsStateWithLifecycle()
    val adminCustomersState by viewModel.adminCustomersState.collectAsStateWithLifecycle()
    val isAdminUser by viewModel.isAdminUser.collectAsStateWithLifecycle()
    val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsStateWithLifecycle()
    val hindiMode by viewModel.hindiFriendlyMode.collectAsStateWithLifecycle()
    val isActionLoading by viewModel.isActionLoading.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var activeOrderSheetService by remember { mutableStateOf<StudioService?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is StudioEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                is StudioEvent.LaunchWhatsAppOrder -> {
                    IntentHelpers.openWhatsApp(context, event.phoneNumber, event.message)
                }
            }
        }
    }

    val tabs = remember {
        listOf(
            BottomTab(StudioRoutes.ROUTE_HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
            BottomTab(StudioRoutes.ROUTE_SERVICES, "Services", Icons.Filled.GridView, Icons.Outlined.GridView),
            BottomTab(StudioRoutes.ROUTE_ORDERS, "Orders", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
            BottomTab(StudioRoutes.ROUTE_STUDIO, "Studio", Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt)
        )
    }

    val cartBadgeCount = cartItems.sumOf { it.quantity }
    val showBottomChrome = currentRoute != StudioRoutes.ROUTE_ADMIN

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(StudioRoutes.ROUTE_HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(ObsidianBlack)) {
        val useNavigationRail = maxWidth >= 600.dp && showBottomChrome

        Row(modifier = Modifier.fillMaxSize()) {
            if (useNavigationRail) {
                NavigationRail(
                    containerColor = AtelierCharcoal,
                    contentColor = IvoryCream,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    tabs.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigateToTab(tab.route) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (tab.route == StudioRoutes.ROUTE_ORDERS && cartBadgeCount > 0) {
                                            Badge(containerColor = ChampagneGold, contentColor = ObsidianBlack) {
                                                Text(cartBadgeCount.toString())
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                }
                            },
                            label = { Text(tab.label) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = ObsidianBlack,
                                selectedTextColor = WarmGold,
                                indicatorColor = ChampagneGold,
                                unselectedIconColor = MutedSilver,
                                unselectedTextColor = MutedSilver
                            )
                        )
                    }
                }
            }

            Scaffold(
                containerColor = ObsidianBlack,
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    if (showBottomChrome && !useNavigationRail) {
                        NavigationBar(
                            containerColor = AtelierCharcoal,
                            contentColor = IvoryCream,
                            windowInsets = WindowInsets.navigationBars
                        ) {
                            tabs.forEach { tab ->
                                val selected = currentRoute == tab.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (tab.route == StudioRoutes.ROUTE_ORDERS && cartBadgeCount > 0) {
                                                    Badge(containerColor = ChampagneGold, contentColor = ObsidianBlack) {
                                                        Text(cartBadgeCount.toString())
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.label
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = tab.label,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ObsidianBlack,
                                        selectedTextColor = WarmGold,
                                        indicatorColor = ChampagneGold,
                                        unselectedIconColor = MutedSilver,
                                        unselectedTextColor = MutedSilver
                                    )
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = StudioRoutes.ROUTE_HOME,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(StudioRoutes.ROUTE_HOME) {
                        HomeScreen(
                            servicesState = servicesState,
                            businessConfig = businessConfig,
                            hindiMode = hindiMode,
                            cartCount = cartBadgeCount,
                            onToggleHindiMode = viewModel::toggleHindiMode,
                            onOpenCart = { navigateToTab(StudioRoutes.ROUTE_ORDERS) },
                            onOpenAdmin = { navController.navigate(StudioRoutes.ROUTE_ADMIN) },
                            onSelectCategory = { cat ->
                                viewModel.selectCategory(cat)
                                navigateToTab(StudioRoutes.ROUTE_SERVICES)
                            },
                            onOrderService = { svc -> activeOrderSheetService = svc },
                            onAddToCart = { svc -> viewModel.addToCart(svc, 1) },
                            onOpenStudioTab = { navigateToTab(StudioRoutes.ROUTE_STUDIO) }
                        )
                    }

                    composable(StudioRoutes.ROUTE_SERVICES) {
                        ServicesScreen(
                            servicesState = servicesState,
                            filteredServices = filteredServices,
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            hindiMode = hindiMode,
                            onSearchQueryChange = viewModel::updateSearchQuery,
                            onSelectCategory = viewModel::selectCategory,
                            onAddToCart = { svc -> viewModel.addToCart(svc, 1) },
                            onOrderService = { svc -> activeOrderSheetService = svc }
                        )
                    }

                    composable(StudioRoutes.ROUTE_ORDERS) {
                        OrdersScreen(
                            cartItems = cartItems,
                            myOrdersState = myOrdersState,
                            businessConfig = businessConfig,
                            defaultCustomerName = currentUserName,
                            hindiMode = hindiMode,
                            isSubmitting = isActionLoading,
                            onUpdateCartQty = viewModel::updateCartQuantity,
                            onRemoveCartItem = viewModel::removeFromCart,
                            onBrowseServices = { navigateToTab(StudioRoutes.ROUTE_SERVICES) },
                            onPlaceCartOrder = { name, phone, req ->
                                viewModel.placeOrderAndLaunchWhatsApp(
                                    customerName = name,
                                    customerPhone = phone,
                                    requirements = req,
                                    items = cartItems,
                                    clearCartOnSuccess = true,
                                    buildMessage = { orderId, items ->
                                        IntentHelpers.buildSingleOrCartOrderMessage(
                                            businessName = businessConfig.businessName,
                                            orderId = orderId,
                                            customerName = name,
                                            customerPhone = phone,
                                            requirements = req,
                                            items = items
                                        )
                                    }
                                )
                            }
                        )
                    }

                    composable(StudioRoutes.ROUTE_STUDIO) {
                        StudioInfoScreen(
                            businessConfig = businessConfig,
                            reviewsState = reviewsState,
                            currentUserName = currentUserName,
                            currentUserEmail = currentUserEmail,
                            isAdminUser = isAdminUser,
                            hindiMode = hindiMode,
                            isSubmittingReview = isActionLoading,
                            onSubmitReview = viewModel::submitReview,
                            onOpenAdminPanel = { navController.navigate(StudioRoutes.ROUTE_ADMIN) },
                            onSignOut = {
                                signOutUser(credentialManager, scope)
                            }
                        )
                    }

                    composable(StudioRoutes.ROUTE_ADMIN) {
                        AdminScreen(
                            isAdminUser = isAdminUser,
                            isAdminUnlocked = isAdminUnlocked,
                            currentUserEmail = currentUserEmail,
                            servicesState = servicesState,
                            adminOrdersState = adminOrdersState,
                            adminCustomersState = adminCustomersState,
                            businessConfig = businessConfig,
                            isActionLoading = isActionLoading,
                            onVerifyPasscode = viewModel::verifyAdminPasscode,
                            onLockAdmin = viewModel::lockAdminPanel,
                            onBack = { navController.popBackStack() },
                            onRestoreCatalog = viewModel::restoreStarterCatalog,
                            onSaveService = viewModel::saveService,
                            onDeleteService = viewModel::deleteService,
                            onUpdateOrderStatus = viewModel::updateOrderStatus,
                            onUpdateCustomer = viewModel::updateCustomer,
                            onSaveBusinessConfig = viewModel::saveBusinessConfig,
                            onUpdateAdminPasscode = viewModel::updateAdminPasscode,
                            onSignOutToSwitchAccount = {
                                signOutUser(credentialManager, scope)
                            }
                        )
                    }
                }
            }
        }
    }

    activeOrderSheetService?.let { service ->
        ServiceOrderBottomSheet(
            service = service,
            defaultCustomerName = currentUserName,
            hindiMode = hindiMode,
            isSubmitting = isActionLoading,
            onDismiss = { activeOrderSheetService = null },
            onAddToCart = { qty -> viewModel.addToCart(service, qty) },
            onConfirmWhatsAppOrder = { name, phone, req, qty ->
                val items = listOf(CartItem(service = service, quantity = qty))
                viewModel.placeOrderAndLaunchWhatsApp(
                    customerName = name,
                    customerPhone = phone,
                    requirements = req,
                    items = items,
                    clearCartOnSuccess = false,
                    buildMessage = { orderId, lineItems ->
                        IntentHelpers.buildSingleOrCartOrderMessage(
                            businessName = businessConfig.businessName,
                            orderId = orderId,
                            customerName = name,
                            customerPhone = phone,
                            requirements = req,
                            items = lineItems
                        )
                    },
                    onComplete = { activeOrderSheetService = null }
                )
            }
        )
    }
}
