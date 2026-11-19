package com.example.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.CartItem
import com.example.data.model.StudioService
import com.example.ui.theme.AtelierCharcoal
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.IvoryCream
import com.example.ui.theme.MutedSilver
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateVelvet
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WhatsAppGreen
import com.example.util.IntentHelpers

@Composable
fun StudioServiceImage(
    imageKey: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    when {
        imageKey.startsWith("data:image") -> {
            val bitmap = remember(imageKey) {
                try {
                    val base64Part = imageKey.substringAfter("base64,", "")
                    if (base64Part.isNotEmpty()) {
                        val bytes = Base64.decode(base64Part, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    } else null
                } catch (_: Exception) {
                    null
                }
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = modifier
                )
            } else {
                FallbackServiceArt(modifier)
            }
        }
        imageKey.startsWith("http://") || imageKey.startsWith("https://") -> {
            AsyncImage(
                model = imageKey,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier
            )
        }
        else -> {
            val resId = when (imageKey) {
                "drawable:img_hero_studio" -> R.drawable.img_hero_studio
                "drawable:img_wedding_shoot" -> R.drawable.img_wedding_shoot
                "drawable:img_printing_studio" -> R.drawable.img_printing_studio
                "drawable:img_studio_logo" -> R.drawable.img_studio_logo
                else -> R.drawable.img_frames_showcase
            }
            Image(
                painter = painterResource(id = resId),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun FallbackServiceArt(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(SlateVelvet),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = null,
            tint = ChampagneGold.copy(alpha = 0.7f),
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun ServiceCatalogCard(
    service: StudioService,
    hindiMode: Boolean,
    onCardClick: () -> Unit,
    onAddToCart: () -> Unit,
    onWhatsAppOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
        border = BorderStroke(1.dp, CardBorderGold),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("service_card_${service.id.ifBlank { service.name }}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    StudioServiceImage(
                        imageKey = service.imageKey,
                        contentDescription = service.name,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (service.popular) {
                        Surface(
                            color = ChampagneGold,
                            shape = RoundedCornerShape(bottomEnd = 10.dp),
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Popular",
                                    tint = ObsidianBlack,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "TOP",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ObsidianBlack
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.categoryEnum.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = ChampagneGold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = IvoryCream,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (hindiMode && service.nameHindi.isNotBlank()) {
                        Text(
                            text = service.nameHindi,
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmGold.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = IntentHelpers.formatInr(service.priceInr),
                            style = MaterialTheme.typography.titleLarge,
                            color = WarmGold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "/ ${service.unitLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSilver
                        )
                    }
                }
            }

            if (service.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSilver,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SubtleDivider)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onAddToCart,
                    border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("add_to_cart_${service.id.ifBlank { service.name }}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (hindiMode) "कार्ट में जोड़ें" else "Add to Cart",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = onWhatsAppOrder,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppGreen,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("whatsapp_order_${service.id.ifBlank { service.name }}")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WhatsApp Order",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceOrderBottomSheet(
    service: StudioService,
    defaultCustomerName: String,
    hindiMode: Boolean,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onAddToCart: (quantity: Int) -> Unit,
    onConfirmWhatsAppOrder: (
        customerName: String,
        customerPhone: String,
        requirements: String,
        quantity: Int
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var quantity by remember { mutableIntStateOf(1) }
    var customerName by remember { mutableStateOf(defaultCustomerName) }
    var customerPhone by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val estimatedTotal = service.priceInr * quantity.toLong()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AtelierCharcoal,
        contentColor = IvoryCream
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (hindiMode) "सर्विस ऑर्डर • SERVICE ORDER" else "SERVICE ORDER",
                        style = MaterialTheme.typography.labelSmall,
                        color = ChampagneGold
                    )
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = IvoryCream
                    )
                    if (hindiMode && service.nameHindi.isNotBlank()) {
                        Text(
                            text = service.nameHindi,
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmGold
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close order sheet",
                        tint = MutedSilver
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quantity & Price Calculator Card
            Surface(
                color = SlateVelvet,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardBorderGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${IntentHelpers.formatInr(service.priceInr)} / ${service.unitLabel}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedSilver
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (hindiMode) {
                                "अनुमानित कुल: ${IntentHelpers.formatInr(estimatedTotal)}"
                            } else {
                                "Estimated Total: ${IntentHelpers.formatInr(estimatedTotal)}"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = ObsidianBlack,
                            border = BorderStroke(1.dp, ChampagneGold)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("sheet_qty_minus")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = "Decrease quantity",
                                    tint = WarmGold
                                )
                            }
                        }
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = IvoryCream,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        Surface(
                            shape = CircleShape,
                            color = ChampagneGold
                        ) {
                            IconButton(
                                onClick = { if (quantity < 500) quantity++ },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("sheet_qty_plus")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Increase quantity",
                                    tint = ObsidianBlack
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampagneGold,
                unfocusedBorderColor = SubtleDivider,
                focusedLabelColor = WarmGold,
                unfocusedLabelColor = MutedSilver,
                focusedTextColor = IvoryCream,
                unfocusedTextColor = IvoryCream,
                cursorColor = ChampagneGold
            )

            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it; validationError = null },
                label = { Text(if (hindiMode) "आपका नाम / Your Name *" else "Your Name *") },
                singleLine = true,
                colors = textFieldColors,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("order_customer_name_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = customerPhone,
                onValueChange = { customerPhone = it; validationError = null },
                label = { Text(if (hindiMode) "फ़ोन नंबर / Phone Number *" else "Phone Number *") },
                placeholder = { Text("+91 98XXXXXXXX") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = textFieldColors,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("order_customer_phone_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = requirements,
                onValueChange = { requirements = it },
                label = {
                    Text(
                        if (hindiMode) "विशेष आवश्यकताएं / Custom Requirements"
                        else "Custom Requirements (Size, Date, Finish)"
                    )
                },
                minLines = 2,
                maxLines = 4,
                colors = textFieldColors,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("order_requirements_input")
            )

            if (validationError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = validationError.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = CrimsonAlert
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onAddToCart(quantity)
                        onDismiss()
                    },
                    border = BorderStroke(1.dp, ChampagneGold),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("sheet_add_to_cart_button")
                ) {
                    Text("Add to Cart", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        val cleanDigits = customerPhone.filter { it.isDigit() }
                        when {
                            customerName.trim().length < 2 -> {
                                validationError = "Please enter your full name."
                            }
                            cleanDigits.length < 7 -> {
                                validationError = "Please enter a valid 10-digit phone number."
                            }
                            else -> {
                                onConfirmWhatsAppOrder(
                                    customerName.trim(),
                                    customerPhone.trim(),
                                    requirements.trim(),
                                    quantity
                                )
                            }
                        }
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppGreen,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(52.dp)
                        .testTag("sheet_send_whatsapp_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSubmitting) "Saving..." else "Order on WhatsApp",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
