package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.BusinessConfig
import com.example.data.model.StudioReview
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
import com.example.ui.viewmodel.UiState
import com.example.util.IntentHelpers

private data class GalleryItem(
    val resId: Int,
    val title: String,
    val subtitle: String
)

@Composable
fun StudioInfoScreen(
    businessConfig: BusinessConfig,
    reviewsState: UiState<List<StudioReview>>,
    currentUserName: String,
    currentUserEmail: String,
    isAdminUser: Boolean,
    hindiMode: Boolean,
    isSubmittingReview: Boolean,
    onSubmitReview: (authorName: String, rating: Int, serviceUsed: String, comment: String, onDone: () -> Unit) -> Unit,
    onOpenAdminPanel: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    var zoomedGalleryItem by remember { mutableStateOf<GalleryItem?>(null) }
    var showWriteReviewDialog by remember { mutableStateOf(false) }
    var legalDialogType by remember { mutableStateOf<String?>(null) }

    val galleryItems = remember {
        listOf(
            GalleryItem(R.drawable.img_wedding_shoot, "Royal Wedding Portrait", "Candid & Golden Hour Bridal Coverage"),
            GalleryItem(R.drawable.img_frames_showcase, "Bespoke Frame Gallery", "Matte Black & Brushed Champagne Gold Frames"),
            GalleryItem(R.drawable.img_printing_studio, "Fine Art Print Lab", "Archival Lustre, Glossy & Restoration Prints"),
            GalleryItem(R.drawable.img_hero_studio, "Studio Pixy Atelier", "Full-Frame Lighting & Portrait Studio")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = if (hindiMode) "स्टूडियो पिक्सी • Studio & Gallery" else "Studio Pixy Atelier",
                    style = MaterialTheme.typography.headlineMedium,
                    color = WarmGold
                )
                Text(
                    text = "Gallery, Reviews, Contact, Policies & Admin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSilver
                )
            }
        }

        // 1. Studio Gallery
        item {
            Column {
                Text(
                    text = if (hindiMode) "स्टूडियो गैलरी • Studio Gallery" else "Studio Gallery",
                    style = MaterialTheme.typography.titleLarge,
                    color = IvoryCream,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(galleryItems) { item ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, CardBorderGold),
                            colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
                            modifier = Modifier
                                .width(230.dp)
                                .clickable { zoomedGalleryItem = item }
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(id = item.resId),
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                )
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = WarmGold
                                    )
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedSilver,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Contact & Location Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
                border = BorderStroke(1.dp, CardBorderGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (hindiMode) "हमारा पता व संपर्क • Contact Us" else "Contact & Studio Location",
                        style = MaterialTheme.typography.titleLarge,
                        color = WarmGold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = businessConfig.businessName, style = MaterialTheme.typography.titleMedium, color = IvoryCream)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = businessConfig.address, style = MaterialTheme.typography.bodyMedium, color = MutedSilver)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Hours: ${businessConfig.openingHours}", style = MaterialTheme.typography.bodySmall, color = ChampagneGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Phone: ${businessConfig.phoneNumber}", style = MaterialTheme.typography.bodySmall, color = IvoryCream)
                    Text(text = "WhatsApp: ${businessConfig.whatsappNumber}", style = MaterialTheme.typography.bodySmall, color = IvoryCream)
                    Text(text = "Email: ${businessConfig.email}", style = MaterialTheme.typography.bodySmall, color = IvoryCream)

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { IntentHelpers.dialPhone(context, businessConfig.phoneNumber) },
                            border = BorderStroke(1.dp, ChampagneGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("studio_call_button")
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call")
                        }
                        Button(
                            onClick = {
                                IntentHelpers.openWhatsApp(
                                    context,
                                    businessConfig.whatsappNumber,
                                    "Hello ${businessConfig.businessName}!"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen, contentColor = ObsidianBlack),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .minimumInteractiveComponentSize()
                                .testTag("studio_whatsapp_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { IntentHelpers.openGoogleMaps(context, businessConfig.googleMapsQuery) },
                            border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.7f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = IvoryCream),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("studio_maps_button")
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Google Maps")
                        }
                        OutlinedButton(
                            onClick = { IntentHelpers.sendEmail(context, businessConfig.email, "Inquiry — ${businessConfig.businessName}") },
                            border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.7f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = IvoryCream),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("studio_email_button")
                        ) {
                            Icon(Icons.Filled.Email, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Email Us")
                        }
                    }
                }
            }
        }

        // 3. About Studio Pixy
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
                border = BorderStroke(1.dp, CardBorderGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (hindiMode) "स्टूडियो पिक्सी के बारे में • About Us" else "About Studio Pixy",
                        style = MaterialTheme.typography.titleLarge,
                        color = WarmGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = businessConfig.aboutDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = IvoryCream.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // 4. Customer Reviews
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (hindiMode) "ग्राहक समीक्षाएं • Reviews" else "Customer Reviews",
                        style = MaterialTheme.typography.titleLarge,
                        color = IvoryCream
                    )
                    OutlinedButton(
                        onClick = { showWriteReviewDialog = true },
                        border = BorderStroke(1.dp, ChampagneGold),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("write_review_button")
                    ) {
                        Icon(Icons.Filled.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Write Review")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                when (reviewsState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ChampagneGold, modifier = Modifier.size(28.dp))
                        }
                    }
                    is UiState.Error -> {
                        Text(text = reviewsState.message, color = CrimsonAlert, style = MaterialTheme.typography.bodySmall)
                    }
                    is UiState.Success -> {
                        if (reviewsState.data.isEmpty()) {
                            Surface(
                                color = AtelierCharcoal,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, CardBorderGold),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No customer reviews yet. Be the first to share your Studio Pixy experience!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MutedSilver,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                reviewsState.data.forEach { review ->
                                    Surface(
                                        color = AtelierCharcoal,
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(1.dp, CardBorderGold),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = review.authorName,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = IvoryCream
                                                )
                                                Row {
                                                    repeat(review.rating.toInt().coerceIn(1, 5)) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Star,
                                                            contentDescription = null,
                                                            tint = WarmGold,
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = review.serviceUsed,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ChampagneGold
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = review.comment,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = IvoryCream.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Share, Legal & Admin / Account Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
                border = BorderStroke(1.dp, CardBorderGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Account, Legal & Admin",
                        style = MaterialTheme.typography.titleMedium,
                        color = WarmGold
                    )
                    Text(
                        text = "Signed in as: ${currentUserName.ifBlank { "Studio Guest" }} (${currentUserEmail.ifBlank { "Google Account" }})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSilver
                    )
                    if (isAdminUser) {
                        Surface(
                            color = ChampagneGold.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, ChampagneGold)
                        ) {
                            Text(
                                text = "Verified Studio Admin Account",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmGold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = SubtleDivider)

                    Button(
                        onClick = onOpenAdminPanel,
                        colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("studio_open_admin_button")
                    ) {
                        Icon(Icons.Filled.AdminPanelSettings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Admin Dashboard", fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                IntentHelpers.shareStudioApp(
                                    context,
                                    businessConfig.businessName,
                                    businessConfig.phoneNumber,
                                    businessConfig.address
                                )
                            },
                            border = BorderStroke(1.dp, SubtleDivider),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = IvoryCream),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share App")
                        }
                        OutlinedButton(
                            onClick = onSignOut,
                            border = BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sign_out_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sign Out")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = { legalDialogType = "PRIVACY" }) {
                            Icon(Icons.Filled.Policy, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Privacy Policy", color = MutedSilver)
                        }
                        TextButton(onClick = { legalDialogType = "TERMS" }) {
                            Icon(Icons.Filled.Description, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Terms & Conditions", color = MutedSilver)
                        }
                    }
                }
            }
        }
    }

    // Lightbox Dialog for Gallery
    zoomedGalleryItem?.let { item ->
        Dialog(onDismissRequest = { zoomedGalleryItem = null }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
                border = BorderStroke(1.dp, ChampagneGold)
            ) {
                Column {
                    Box {
                        Image(
                            painter = painterResource(id = item.resId),
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        IconButton(
                            onClick = { zoomedGalleryItem = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(ObsidianBlack.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Close preview", tint = IvoryCream)
                        }
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = item.title, style = MaterialTheme.typography.titleLarge, color = WarmGold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = item.subtitle, style = MaterialTheme.typography.bodyMedium, color = IvoryCream)
                    }
                }
            }
        }
    }

    // Write Review Dialog
    if (showWriteReviewDialog) {
        WriteReviewDialog(
            defaultName = currentUserName,
            isSubmitting = isSubmittingReview,
            onDismiss = { showWriteReviewDialog = false },
            onSubmit = { name, rating, serviceUsed, comment ->
                onSubmitReview(name, rating, serviceUsed, comment) {
                    showWriteReviewDialog = false
                }
            }
        )
    }

    // Privacy Policy / Terms Dialog
    legalDialogType?.let { type ->
        AlertDialog(
            onDismissRequest = { legalDialogType = null },
            containerColor = AtelierCharcoal,
            titleContentColor = WarmGold,
            textContentColor = IvoryCream,
            title = {
                Text(if (type == "PRIVACY") "Privacy Policy" else "Terms & Conditions")
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = if (type == "PRIVACY") {
                            "Studio Pixy respects your privacy. Your Google Sign-In details, name, phone number, and order notes are stored securely in Cloud Firestore solely to process your photography bookings, photo frames, and printing orders. You initiate WhatsApp messages directly from your device. We never sell or share customer data with third parties."
                        } else {
                            "All prices shown in Indian Rupees (₹) are subject to studio confirmation on custom sizes, bulk prints, or multi-day wedding packages. Estimated totals calculated in the app help speed up your WhatsApp inquiry. Urgent passport photos are subject to studio operating hours. Completed orders and custom-framed items are inspected at pickup."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { legalDialogType = null }) {
                    Text("Close", color = ChampagneGold)
                }
            }
        )
    }
}

@Composable
private fun WriteReviewDialog(
    defaultName: String,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf(defaultName) }
    var serviceUsed by remember { mutableStateOf("Photo Frame / Passport Photo") }
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var error by remember { mutableStateOf<String?>(null) }

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
        title = { Text("Rate Studio Pixy") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (star in 1..5) {
                        IconButton(
                            onClick = { rating = star },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (star <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "$star stars",
                                tint = WarmGold
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = null },
                    label = { Text("Your Name") },
                    singleLine = true,
                    colors = tfColors
                )
                OutlinedTextField(
                    value = serviceUsed,
                    onValueChange = { serviceUsed = it },
                    label = { Text("Service Used") },
                    singleLine = true,
                    colors = tfColors
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it; error = null },
                    label = { Text("Your Review") },
                    minLines = 3,
                    colors = tfColors
                )
                if (error != null) {
                    Text(text = error.orEmpty(), color = CrimsonAlert, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        name.trim().isBlank() -> error = "Enter your name"
                        comment.trim().length < 2 -> error = "Write a short review"
                        else -> onSubmit(name.trim(), rating, serviceUsed.trim(), comment.trim())
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBlack)
            ) {
                Text(if (isSubmitting) "Posting..." else "Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedSilver)
            }
        }
    )
}
