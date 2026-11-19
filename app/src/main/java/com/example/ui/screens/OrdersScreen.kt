package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.BusinessConfig
import com.example.data.model.CartItem
import com.example.data.model.OrderStatus
import com.example.data.model.StudioOrder
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

@Composable
fun OrdersScreen(
    cartItems: List<CartItem>,
    myOrdersState: UiState<List<StudioOrder>>,
    businessConfig: BusinessConfig,
    defaultCustomerName: String,
    hindiMode: Boolean,
    isSubmitting: Boolean,
    onUpdateCartQty: (serviceId: String, newQty: Int) -> Unit,
    onRemoveCartItem: (serviceId: String) -> Unit,
    onBrowseServices: () -> Unit,
    onPlaceCartOrder: (name: String, phone: String, requirements: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = if (hindiMode) "कार्ट व मेरे ऑर्डर • Cart & Orders" else "Cart & My Orders",
                style = MaterialTheme.typography.headlineMedium,
                color = WarmGold
            )
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = AtelierCharcoal,
            contentColor = WarmGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ChampagneGold
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Cart (${cartItems.sumOf { it.quantity }})") },
                modifier = Modifier.testTag("tab_cart")
            )
            val orderCount = (myOrdersState as? UiState.Success)?.data?.size ?: 0
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("My Orders ($orderCount)") },
                modifier = Modifier.testTag("tab_my_orders")
            )
        }

        if (selectedTab == 0) {
            CartCheckoutTab(
                cartItems = cartItems,
                defaultCustomerName = defaultCustomerName,
                hindiMode = hindiMode,
                isSubmitting = isSubmitting,
                onUpdateCartQty = onUpdateCartQty,
                onRemoveCartItem = onRemoveCartItem,
                onBrowseServices = onBrowseServices,
                onPlaceOrder = { name, phone, req ->
                    onPlaceCartOrder(name, phone, req)
                    selectedTab = 1
                }
            )
        } else {
            MyOrdersHistoryTab(
                myOrdersState = myOrdersState,
                onResendWhatsApp = { order ->
                    val msg = IntentHelpers.buildExistingOrderMessage(businessConfig.businessName, order)
                    IntentHelpers.openWhatsApp(context, businessConfig.whatsappNumber, msg)
                }
            )
        }
    }
}

@Composable
private fun CartCheckoutTab(
    cartItems: List<CartItem>,
    defaultCustomerName: String,
    hindiMode: Boolean,
    isSubmitting: Boolean,
    onUpdateCartQty: (String, Int) -> Unit,
    onRemoveCartItem: (String) -> Unit,
    onBrowseServices: () -> Unit,
    onPlaceOrder: (String, String, String) -> Unit
) {
    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = ChampagneGold.copy(alpha = 0.6f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (hindiMode) "आपका कार्ट खाली है" else "Your Studio Cart is Empty",
                    style = MaterialTheme.typography.titleLarge,
                    color = IvoryCream
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Add photo frames, passport packages, or printing services to order via WhatsApp.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSilver
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onBrowseServices,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("empty_cart_browse_button")
                ) {
                    Text("Browse Services", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    var customerName by remember { mutableStateOf(defaultCustomerName) }
    var customerPhone by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val totalEstimate = cartItems.sumOf { it.lineTotalInr }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cartItems, key = { it.service.id.ifBlank { it.service.name } }) { item ->
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
                        imageKey = item.service.imageKey,
                        contentDescription = item.service.name,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.service.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = IvoryCream,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${IntentHelpers.formatInr(item.service.priceInr)} × ${item.quantity} = ${IntentHelpers.formatInr(item.lineTotalInr)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmGold,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = SlateVelvet,
                                border = BorderStroke(1.dp, CardBorderGold)
                            ) {
                                IconButton(
                                    onClick = { onUpdateCartQty(item.service.id, item.quantity - 1) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Decrease", tint = WarmGold, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = item.quantity.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                color = IvoryCream,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Surface(
                                shape = CircleShape,
                                color = ChampagneGold
                            ) {
                                IconButton(
                                    onClick = { onUpdateCartQty(item.service.id, item.quantity + 1) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Increase", tint = ObsidianBlack, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    IconButton(
                        onClick = { onRemoveCartItem(item.service.id) },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "Remove item",
                            tint = CrimsonAlert
                        )
                    }
                }
            }
        }

        // Checkout Form Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (hindiMode) "अनुमानित कुल राशि" else "Estimated Total",
                            style = MaterialTheme.typography.titleMedium,
                            color = IvoryCream
                        )
                        Text(
                            text = IntentHelpers.formatInr(totalEstimate),
                            style = MaterialTheme.typography.headlineMedium,
                            color = WarmGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = SubtleDivider)
                    Spacer(modifier = Modifier.height(14.dp))

                    val tfColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = SubtleDivider,
                        focusedLabelColor = WarmGold,
                        unfocusedLabelColor = MutedSilver,
                        focusedTextColor = IvoryCream,
                        unfocusedTextColor = IvoryCream
                    )

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it; validationError = null },
                        label = { Text("Customer Name *") },
                        singleLine = true,
                        colors = tfColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cart_customer_name_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it; validationError = null },
                        label = { Text("Phone / WhatsApp Number *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = tfColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cart_customer_phone_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = requirements,
                        onValueChange = { requirements = it },
                        label = { Text("Custom Requirements (Frame color, urgent delivery, etc.)") },
                        minLines = 2,
                        maxLines = 4,
                        colors = tfColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cart_requirements_input")
                    )

                    if (validationError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = validationError.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = CrimsonAlert
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val digits = customerPhone.filter { it.isDigit() }
                            when {
                                customerName.trim().length < 2 -> validationError = "Please enter your name."
                                digits.length < 7 -> validationError = "Please enter a valid phone number."
                                else -> onPlaceOrder(customerName.trim(), customerPhone.trim(), requirements.trim())
                            }
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WhatsAppGreen,
                            contentColor = ObsidianBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("place_cart_whatsapp_order_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSubmitting) "Placing Order..." else "Place Order & Send on WhatsApp",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyOrdersHistoryTab(
    myOrdersState: UiState<List<StudioOrder>>,
    onResendWhatsApp: (StudioOrder) -> Unit
) {
    when (myOrdersState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ChampagneGold)
            }
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = myOrdersState.message, color = CrimsonAlert)
            }
        }
        is UiState.Success -> {
            if (myOrdersState.data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = ChampagneGold.copy(alpha = 0.6f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No orders placed yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = IvoryCream
                        )
                        Text(
                            text = "Your confirmed & pending studio orders will appear here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSilver
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(myOrdersState.data, key = { it.id }) { order ->
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
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Order #${order.id.takeLast(8)}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = WarmGold
                                    )
                                    Surface(
                                        color = statusColor.copy(alpha = 0.18f),
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, statusColor)
                                    ) {
                                        Text(
                                            text = order.statusEnum.displayName.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = statusColor,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                order.items.forEach { line ->
                                    Text(
                                        text = "• ${line.serviceName} × ${line.quantity} (${IntentHelpers.formatInr(line.lineTotalInr)})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = IvoryCream
                                    )
                                }
                                if (order.requirements.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Note: ${order.requirements}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedSilver
                                    )
                                }
                                if (order.adminNotes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Studio Update: ${order.adminNotes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ChampagneGold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total: ${IntentHelpers.formatInr(order.totalAmountInr)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = WarmGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                    OutlinedButton(
                                        onClick = { onResendWhatsApp(order) },
                                        border = BorderStroke(1.dp, WhatsAppGreen),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreen),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("WhatsApp", style = MaterialTheme.typography.labelMedium)
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
