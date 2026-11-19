package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.DefaultStudioSeed
import com.example.data.model.BusinessConfig
import com.example.data.model.CustomerProfile
import com.example.data.model.OrderStatus
import com.example.data.model.ServiceCategory
import com.example.data.model.StudioOrder
import com.example.data.model.StudioService
import com.example.ui.components.StudioServiceImage
import com.example.ui.theme.AmberPending
import com.example.ui.theme.AtelierCharcoal
import com.example.ui.theme.BlueConfirmed
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldCompleted
import com.example.ui.theme.IvoryCream
import com.example.ui.theme.MutedSilver
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateVelvet
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.UiState
import com.example.util.IntentHelpers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    isAdminUser: Boolean,
    isAdminUnlocked: Boolean,
    currentUserEmail: String,
    servicesState: UiState<List<StudioService>>,
    adminOrdersState: UiState<List<StudioOrder>>,
    adminCustomersState: UiState<List<CustomerProfile>>,
    businessConfig: BusinessConfig,
    isActionLoading: Boolean,
    onVerifyPasscode: (String) -> Boolean,
    onLockAdmin: () -> Unit,
    onBack: () -> Unit,
    onRestoreCatalog: () -> Unit,
    onSaveService: (StudioService, () -> Unit) -> Unit,
    onDeleteService: (StudioService) -> Unit,
    onUpdateOrderStatus: (String, OrderStatus, String) -> Unit,
    onUpdateCustomer: (String, String, String, String, () -> Unit) -> Unit,
    onSaveBusinessConfig: (BusinessConfig) -> Unit,
    onUpdateAdminPasscode: (String) -> Unit,
    onSignOutToSwitchAccount: () -> Unit
) {
    BackHandler {
        onBack()
    }

    Scaffold(
        containerColor = ObsidianBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Studio Pixy Admin",
                        style = MaterialTheme.typography.titleLarge,
                        color = WarmGold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Studio",
                            tint = IvoryCream
                        )
                    }
                },
                actions = {
                    if (isAdminUnlocked) {
                        TextButton(onClick = onLockAdmin) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Lock", color = WarmGold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AtelierCharcoal)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!isAdminUnlocked) {
                AdminLoginGate(
                    isAdminUser = isAdminUser,
                    currentUserEmail = currentUserEmail,
                    onVerifyPasscode = onVerifyPasscode,
                    onSignOutToSwitchAccount = onSignOutToSwitchAccount
                )
            } else {
                AdminDashboardTabs(
                    isAdminUser = isAdminUser,
                    currentUserEmail = currentUserEmail,
                    servicesState = servicesState,
                    adminOrdersState = adminOrdersState,
                    adminCustomersState = adminCustomersState,
                    businessConfig = businessConfig,
                    isActionLoading = isActionLoading,
                    onRestoreCatalog = onRestoreCatalog,
                    onSaveService = onSaveService,
                    onDeleteService = onDeleteService,
                    onUpdateOrderStatus = onUpdateOrderStatus,
                    onUpdateCustomer = onUpdateCustomer,
                    onSaveBusinessConfig = onSaveBusinessConfig,
                    onUpdateAdminPasscode = onUpdateAdminPasscode,
                    onSignOutToSwitchAccount = onSignOutToSwitchAccount
                )
            }
        }
    }
}

@Composable
private fun AdminLoginGate(
    isAdminUser: Boolean,
    currentUserEmail: String,
    onVerifyPasscode: (String) -> Boolean,
    onSignOutToSwitchAccount: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
            border = BorderStroke(1.dp, ChampagneGold),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Admin Authentication",
                    style = MaterialTheme.typography.headlineMedium,
                    color = WarmGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Signed in as: ${currentUserEmail.ifBlank { "Google User" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSilver
                )
                if (!isAdminUser) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = AmberPending.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AmberPending.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Cloud Admin writes are restricted to studio owner (${DefaultStudioSeed.ADMIN_EMAIL}).",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmberPending
                            )
                            TextButton(onClick = onSignOutToSwitchAccount) {
                                Text("Switch Google Account", color = WarmGold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMsg = null },
                    label = { Text("Admin Password") },
                    placeholder = { Text("Default: ${DefaultStudioSeed.DEFAULT_ADMIN_PASSCODE}") },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Toggle password",
                                tint = MutedSilver
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = SubtleDivider,
                        focusedTextColor = IvoryCream,
                        unfocusedTextColor = IvoryCream,
                        focusedLabelColor = WarmGold,
                        unfocusedLabelColor = MutedSilver
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input")
                )
                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMsg.orEmpty(), color = CrimsonAlert, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = {
                        if (!onVerifyPasscode(password)) {
                            errorMsg = "Incorrect admin password. Default is '${DefaultStudioSeed.DEFAULT_ADMIN_PASSCODE}'."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBlack),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admin_unlock_button")
                ) {
                    Text("Unlock Admin Dashboard", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminDashboardTabs(
    isAdminUser: Boolean,
    currentUserEmail: String,
    servicesState: UiState<List<StudioService>>,
    adminOrdersState: UiState<List<StudioOrder>>,
    adminCustomersState: UiState<List<CustomerProfile>>,
    businessConfig: BusinessConfig,
    isActionLoading: Boolean,
    onRestoreCatalog: () -> Unit,
    onSaveService: (StudioService, () -> Unit) -> Unit,
    onDeleteService: (StudioService) -> Unit,
    onUpdateOrderStatus: (String, OrderStatus, String) -> Unit,
    onUpdateCustomer: (String, String, String, String, () -> Unit) -> Unit,
    onSaveBusinessConfig: (BusinessConfig) -> Unit,
    onUpdateAdminPasscode: (String) -> Unit,
    onSignOutToSwitchAccount: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Services & Prices", "Orders", "Customers", "Business Info")

    Column(modifier = Modifier.fillMaxSize()) {
        if (!isAdminUser) {
            Surface(
                color = CrimsonAlert.copy(alpha = 0.16f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Read-only preview ($currentUserEmail). Sign in as ${DefaultStudioSeed.ADMIN_EMAIL} to save changes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CrimsonAlert,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onSignOutToSwitchAccount) {
                        Text("Switch", color = WarmGold)
                    }
                }
            }
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = AtelierCharcoal,
            contentColor = WarmGold,
            edgePadding = 12.dp,
            indicator = { positions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(positions[selectedTab]),
                    color = ChampagneGold
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                    modifier = Modifier.testTag("admin_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> AdminServicesTab(
                servicesState = servicesState,
                isActionLoading = isActionLoading,
                onRestoreCatalog = onRestoreCatalog,
                onSaveService = onSaveService,
                onDeleteService = onDeleteService
            )
            1 -> AdminOrdersTab(
                adminOrdersState = adminOrdersState,
                businessConfig = businessConfig,
                isActionLoading = isActionLoading,
                onUpdateOrderStatus = onUpdateOrderStatus
            )
            2 -> AdminCustomersTab(
                adminCustomersState = adminCustomersState,
                onUpdateCustomer = onUpdateCustomer
            )
            3 -> AdminBusinessInfoTab(
                businessConfig = businessConfig,
                isActionLoading = isActionLoading,
                onSaveBusinessConfig = onSaveBusinessConfig,
                onUpdateAdminPasscode = onUpdateAdminPasscode
            )
        }
    }
}

@Composable
private fun AdminServicesTab(
    servicesState: UiState<List<StudioService>>,
    isActionLoading: Boolean,
    onRestoreCatalog: () -> Unit,
    onSaveService: (StudioService, () -> Unit) -> Unit,
    onDeleteService: (StudioService) -> Unit
) {
    var editingService by remember { mutableStateOf<StudioService?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var filterCategory by remember { mutableStateOf<ServiceCategory?>(null) }
    var deleteConfirmService by remember { mutableStateOf<StudioService?>(null) }

    val services = (servicesState as? UiState.Success)?.data ?: emptyList()
    val filtered = services.filter { filterCategory == null || it.categoryEnum == filterCategory }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    editingService = StudioService(
                        category = (filterCategory ?: ServiceCategory.PHOTO_FRAMES).name,
                        sortOrder = (services.size + 1).toLong()
                    )
                    showEditor = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBlack),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("admin_add_service_button")
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Service", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onRestoreCatalog,
                enabled = !isActionLoading,
                border = BorderStroke(1.dp, ChampagneGold),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("admin_seed_catalog_button")
            ) {
                Icon(Icons.Filled.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Seed 27")
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = filterCategory == null,
                    onClick = { filterCategory = null },
                    label = { Text("All (${services.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ChampagneGold,
                        selectedLabelColor = ObsidianBlack,
                        containerColor = SlateVelvet,
                        labelColor = IvoryCream
                    )
                )
            }
            items(ServiceCategory.entries) { cat ->
                FilterChip(
                    selected = filterCategory == cat,
                    onClick = { filterCategory = if (filterCategory == cat) null else cat },
                    label = { Text(cat.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ChampagneGold,
                        selectedLabelColor = ObsidianBlack,
                        containerColor = SlateVelvet,
                        labelColor = IvoryCream
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered, key = { it.id.ifBlank { it.name } }) { service ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
                    border = BorderStroke(1.dp, CardBorderGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StudioServiceImage(
                            imageKey = service.imageKey,
                            contentDescription = service.name,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = service.name, style = MaterialTheme.typography.titleSmall, color = IvoryCream)
                            Text(
                                text = "${IntentHelpers.formatInr(service.priceInr)} / ${service.unitLabel}",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarmGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = service.categoryEnum.displayName + if (!service.active) " (Hidden)" else "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedSilver
                            )
                        }
                        IconButton(
                            onClick = {
                                editingService = service
                                showEditor = true
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit service", tint = ChampagneGold)
                        }
                        IconButton(
                            onClick = { deleteConfirmService = service },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete service", tint = CrimsonAlert)
                        }
                    }
                }
            }
        }
    }

    if (showEditor && editingService != null) {
        ServiceEditorDialog(
            initial = editingService!!,
            isSaving = isActionLoading,
            onDismiss = { showEditor = false },
            onSave = { updated ->
                onSaveService(updated) {
                    showEditor = false
                }
            }
        )
    }

    deleteConfirmService?.let { svc ->
        AlertDialog(
            onDismissRequest = { deleteConfirmService = null },
            containerColor = AtelierCharcoal,
            titleContentColor = WarmGold,
            textContentColor = IvoryCream,
            title = { Text("Delete Service?") },
            text = { Text("Remove '${svc.name}' from the live Studio Pixy catalog?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteService(svc)
                        deleteConfirmService = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmService = null }) {
                    Text("Cancel", color = MutedSilver)
                }
            }
        )
    }
}

@Composable
private fun ServiceEditorDialog(
    initial: StudioService,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (StudioService) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(initial.name) }
    var nameHindi by remember { mutableStateOf(initial.nameHindi) }
    var category by remember { mutableStateOf(initial.categoryEnum) }
    var priceText by remember { mutableStateOf(initial.priceInr.toString()) }
    var unitLabel by remember { mutableStateOf(initial.unitLabel) }
    var description by remember { mutableStateOf(initial.description) }
    var imageKey by remember { mutableStateOf(initial.imageKey) }
    var popular by remember { mutableStateOf(initial.popular) }
    var active by remember { mutableStateOf(initial.active) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val compressed = IntentHelpers.uriToCompressedDataUrl(context, uri)
                if (compressed != null) {
                    imageKey = compressed
                } else {
                    errorMsg = "Could not compress selected image; please choose a smaller image."
                }
            }
        }
    )

    val tfColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ChampagneGold,
        unfocusedBorderColor = SubtleDivider,
        focusedLabelColor = WarmGold,
        unfocusedLabelColor = MutedSilver,
        focusedTextColor = IvoryCream,
        unfocusedTextColor = IvoryCream
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AtelierCharcoal,
        titleContentColor = WarmGold,
        textContentColor = IvoryCream,
        title = { Text(if (initial.id.isBlank()) "Add New Service" else "Edit Service & Price") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Photo Upload Preview & Picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StudioServiceImage(
                        imageKey = imageKey,
                        contentDescription = "Service preview",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        border = BorderStroke(1.dp, ChampagneGold),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold),
                        modifier = Modifier.testTag("admin_upload_service_photo_button")
                    ) {
                        Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Photo")
                    }
                }

                // Category Selector
                Text("Category", style = MaterialTheme.typography.labelMedium, color = ChampagneGold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ServiceCategory.entries) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.displayName, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ChampagneGold,
                                selectedLabelColor = ObsidianBlack,
                                containerColor = SlateVelvet,
                                labelColor = IvoryCream
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMsg = null },
                    label = { Text("Service Name *") },
                    singleLine = true,
                    colors = tfColors,
                    modifier = Modifier.fillMaxWidth().testTag("admin_service_name_input")
                )
                OutlinedTextField(
                    value = nameHindi,
                    onValueChange = { nameHindi = it },
                    label = { Text("Hindi Subtitle (optional)") },
                    singleLine = true,
                    colors = tfColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it.filter { ch -> ch.isDigit() }; errorMsg = null },
                        label = { Text("Price (₹) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = tfColors,
                        modifier = Modifier.weight(1f).testTag("admin_service_price_input")
                    )
                    OutlinedTextField(
                        value = unitLabel,
                        onValueChange = { unitLabel = it },
                        label = { Text("Unit") },
                        singleLine = true,
                        colors = tfColors,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 2,
                    colors = tfColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Highlight as Popular", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = popular,
                        onCheckedChange = { popular = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBlack, checkedTrackColor = ChampagneGold)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Visible to Customers", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = active,
                        onCheckedChange = { active = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBlack, checkedTrackColor = ChampagneGold)
                    )
                }

                if (errorMsg != null) {
                    Text(text = errorMsg.orEmpty(), color = CrimsonAlert, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toLongOrNull()
                    when {
                        name.trim().isBlank() -> errorMsg = "Enter service name"
                        price == null || price < 0L -> errorMsg = "Enter a valid ₹ price"
                        else -> onSave(
                            initial.copy(
                                name = name.trim(),
                                nameHindi = nameHindi.trim(),
                                category = category.name,
                                priceInr = price,
                                unitLabel = unitLabel.trim().ifBlank { "per item" },
                                description = description.trim(),
                                imageKey = imageKey,
                                popular = popular,
                                active = active
                            )
                        )
                    }
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("admin_save_service_button")
            ) {
                Text(if (isSaving) "Saving..." else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedSilver)
            }
        }
    )
}

@Composable
private fun AdminOrdersTab(
    adminOrdersState: UiState<List<StudioOrder>>,
    businessConfig: BusinessConfig,
    isActionLoading: Boolean,
    onUpdateOrderStatus: (String, OrderStatus, String) -> Unit
) {
    val context = LocalContext.current
    var statusFilter by remember { mutableStateOf<OrderStatus?>(null) }

    when (adminOrdersState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ChampagneGold)
            }
        }
        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(text = adminOrdersState.message, color = CrimsonAlert)
            }
        }
        is UiState.Success -> {
            val orders = adminOrdersState.data.filter { statusFilter == null || it.statusEnum == statusFilter }
            Column(modifier = Modifier.fillMaxSize()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = statusFilter == null,
                            onClick = { statusFilter = null },
                            label = { Text("All (${adminOrdersState.data.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ChampagneGold,
                                selectedLabelColor = ObsidianBlack,
                                containerColor = SlateVelvet,
                                labelColor = IvoryCream
                            )
                        )
                    }
                    items(OrderStatus.entries) { status ->
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { statusFilter = if (statusFilter == status) null else status },
                            label = { Text(status.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ChampagneGold,
                                selectedLabelColor = ObsidianBlack,
                                containerColor = SlateVelvet,
                                labelColor = IvoryCream
                            )
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        var adminNote by remember(order.id, order.adminNotes) { mutableStateOf(order.adminNotes) }
                        val statusColor = when (order.statusEnum) {
                            OrderStatus.PENDING -> AmberPending
                            OrderStatus.CONFIRMED -> BlueConfirmed
                            OrderStatus.COMPLETED -> EmeraldCompleted
                        }
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
                            border = BorderStroke(1.dp, CardBorderGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${order.customerName} (${order.customerPhone})",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = IvoryCream
                                        )
                                        Text(
                                            text = "#${order.id} • ${IntentHelpers.formatInr(order.totalAmountInr)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = WarmGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Surface(
                                        color = statusColor.copy(alpha = 0.16f),
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, statusColor)
                                    ) {
                                        Text(
                                            text = order.statusEnum.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = statusColor,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                order.items.forEach { item ->
                                    Text(
                                        text = "• ${item.serviceName} × ${item.quantity} = ${IntentHelpers.formatInr(item.lineTotalInr)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = IvoryCream
                                    )
                                }
                                if (order.requirements.isNotBlank()) {
                                    Text(
                                        text = "Customer Note: ${order.requirements}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedSilver
                                    )
                                }

                                OutlinedTextField(
                                    value = adminNote,
                                    onValueChange = { adminNote = it },
                                    label = { Text("Studio Admin Note (optional)") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ChampagneGold,
                                        unfocusedBorderColor = SubtleDivider,
                                        focusedTextColor = IvoryCream,
                                        unfocusedTextColor = IvoryCream
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OrderStatus.entries.forEach { targetStatus ->
                                        val isCurrent = order.statusEnum == targetStatus
                                        OutlinedButton(
                                            onClick = { onUpdateOrderStatus(order.id, targetStatus, adminNote) },
                                            enabled = !isActionLoading,
                                            border = BorderStroke(
                                                1.dp,
                                                if (isCurrent) ChampagneGold else SubtleDivider
                                            ),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = if (isCurrent) ChampagneGold.copy(alpha = 0.2f) else ObsidianBlack,
                                                contentColor = if (isCurrent) WarmGold else MutedSilver
                                            ),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(targetStatus.displayName, style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { IntentHelpers.dialPhone(context, order.customerPhone) },
                                        border = BorderStroke(1.dp, ChampagneGold),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Call")
                                    }
                                    Button(
                                        onClick = {
                                            IntentHelpers.openWhatsApp(
                                                context,
                                                order.customerPhone,
                                                "Hello ${order.customerName}, update from ${businessConfig.businessName} for Order #${order.id}: Status is ${order.statusEnum.displayName}. ${adminNote}"
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen, contentColor = ObsidianBlack),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("WhatsApp", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminCustomersTab(
    adminCustomersState: UiState<List<CustomerProfile>>,
    onUpdateCustomer: (String, String, String, String, () -> Unit) -> Unit
) {
    val context = LocalContext.current
    var editingCustomer by remember { mutableStateOf<CustomerProfile?>(null) }

    when (adminCustomersState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ChampagneGold)
            }
        }
        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(text = adminCustomersState.message, color = CrimsonAlert)
            }
        }
        is UiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(adminCustomersState.data, key = { it.userId.ifBlank { it.id } }) { customer ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
                        border = BorderStroke(1.dp, CardBorderGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = customer.displayName, style = MaterialTheme.typography.titleSmall, color = IvoryCream)
                                    Text(text = "${customer.phone} • ${customer.email}", style = MaterialTheme.typography.bodySmall, color = MutedSilver)
                                    Text(text = "Orders placed: ${customer.orderCount}", style = MaterialTheme.typography.labelSmall, color = WarmGold)
                                }
                                IconButton(onClick = { editingCustomer = customer }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit customer", tint = ChampagneGold)
                                }
                            }
                            if (customer.notes.isNotBlank()) {
                                Text(text = "Notes: ${customer.notes}", style = MaterialTheme.typography.bodySmall, color = ChampagneGold)
                            }
                            if (customer.phone.isNotBlank()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { IntentHelpers.dialPhone(context, customer.phone) },
                                        border = BorderStroke(1.dp, ChampagneGold),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Call")
                                    }
                                    Button(
                                        onClick = {
                                            IntentHelpers.openWhatsApp(context, customer.phone, "Hello ${customer.displayName} from Studio Pixy!")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen, contentColor = ObsidianBlack),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("WhatsApp", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    editingCustomer?.let { cust ->
        var name by remember { mutableStateOf(cust.displayName) }
        var phone by remember { mutableStateOf(cust.phone) }
        var notes by remember { mutableStateOf(cust.notes) }

        AlertDialog(
            onDismissRequest = { editingCustomer = null },
            containerColor = AtelierCharcoal,
            titleContentColor = WarmGold,
            textContentColor = IvoryCream,
            title = { Text("Manage Customer Info") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true)
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, singleLine = true)
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Studio Notes") }, minLines = 2)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateCustomer(cust.userId, name, phone, notes) {
                            editingCustomer = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBlack)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCustomer = null }) {
                    Text("Cancel", color = MutedSilver)
                }
            }
        )
    }
}

@Composable
private fun AdminBusinessInfoTab(
    businessConfig: BusinessConfig,
    isActionLoading: Boolean,
    onSaveBusinessConfig: (BusinessConfig) -> Unit,
    onUpdateAdminPasscode: (String) -> Unit
) {
    var businessName by remember(businessConfig) { mutableStateOf(businessConfig.businessName) }
    var tagline by remember(businessConfig) { mutableStateOf(businessConfig.tagline) }
    var whatsappNumber by remember(businessConfig) { mutableStateOf(businessConfig.whatsappNumber) }
    var phoneNumber by remember(businessConfig) { mutableStateOf(businessConfig.phoneNumber) }
    var address by remember(businessConfig) { mutableStateOf(businessConfig.address) }
    var email by remember(businessConfig) { mutableStateOf(businessConfig.email) }
    var openingHours by remember(businessConfig) { mutableStateOf(businessConfig.openingHours) }
    var mapsQuery by remember(businessConfig) { mutableStateOf(businessConfig.googleMapsQuery) }
    var offerBadge by remember(businessConfig) { mutableStateOf(businessConfig.offerBadge) }
    var offerDescription by remember(businessConfig) { mutableStateOf(businessConfig.offerDescription) }
    var aboutDescription by remember(businessConfig) { mutableStateOf(businessConfig.aboutDescription) }
    var newPasscode by remember { mutableStateOf("") }

    val tfColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ChampagneGold,
        unfocusedBorderColor = SubtleDivider,
        focusedLabelColor = WarmGold,
        unfocusedLabelColor = MutedSilver,
        focusedTextColor = IvoryCream,
        unfocusedTextColor = IvoryCream
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Contact Details, Hours & Offers",
            style = MaterialTheme.typography.titleLarge,
            color = WarmGold
        )
        OutlinedTextField(value = businessName, onValueChange = { businessName = it }, label = { Text("Business Name") }, colors = tfColors, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = tagline, onValueChange = { tagline = it }, label = { Text("Tagline") }, colors = tfColors, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = whatsappNumber,
            onValueChange = { whatsappNumber = it },
            label = { Text("WhatsApp Order Number (e.g. +919876543210)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = tfColors,
            modifier = Modifier.fillMaxWidth().testTag("admin_whatsapp_number_input")
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Call Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = tfColors,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Studio Address") }, minLines = 2, colors = tfColors, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Business Email") }, colors = tfColors, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = openingHours, onValueChange = { openingHours = it }, label = { Text("Studio Opening Hours") }, colors = tfColors, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = mapsQuery, onValueChange = { mapsQuery = it }, label = { Text("Google Maps Location Query") }, colors = tfColors, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = offerBadge, onValueChange = { offerBadge = it }, label = { Text("Special Offer Badge") }, colors = tfColors, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = offerDescription, onValueChange = { offerDescription = it }, label = { Text("Special Offer Description") }, minLines = 2, colors = tfColors, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = aboutDescription, onValueChange = { aboutDescription = it }, label = { Text("About Studio Pixy") }, minLines = 3, colors = tfColors, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                onSaveBusinessConfig(
                    businessConfig.copy(
                        businessName = businessName,
                        tagline = tagline,
                        whatsappNumber = whatsappNumber,
                        phoneNumber = phoneNumber,
                        address = address,
                        email = email,
                        openingHours = openingHours,
                        googleMapsQuery = mapsQuery,
                        offerBadge = offerBadge,
                        offerDescription = offerDescription,
                        aboutDescription = aboutDescription
                    )
                )
            },
            enabled = !isActionLoading,
            colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBlack),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("admin_save_business_button")
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isActionLoading) "Saving..." else "Save Business Details", fontWeight = FontWeight.Bold)
        }

        HorizontalDivider(color = SubtleDivider, modifier = Modifier.padding(vertical = 8.dp))

        Text("Change Admin Lock Passcode", style = MaterialTheme.typography.titleMedium, color = IvoryCream)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newPasscode,
                onValueChange = { newPasscode = it },
                label = { Text("New Passcode (min 4 chars)") },
                singleLine = true,
                colors = tfColors,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = {
                    onUpdateAdminPasscode(newPasscode)
                    newPasscode = ""
                },
                border = BorderStroke(1.dp, ChampagneGold),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold)
            ) {
                Text("Update")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
