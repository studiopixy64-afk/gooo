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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.FilterFrames
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.DefaultStudioSeed
import com.example.data.model.BusinessConfig
import com.example.data.model.ServiceCategory
import com.example.data.model.StudioService
import com.example.ui.components.StudioServiceImage
import com.example.ui.theme.AtelierCharcoal
import com.example.ui.theme.BurnishedBronze
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.IvoryCream
import com.example.ui.theme.MutedSilver
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateVelvet
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.UiState
import com.example.util.IntentHelpers

@Composable
fun HomeScreen(
    servicesState: UiState<List<StudioService>>,
    businessConfig: BusinessConfig,
    hindiMode: Boolean,
    cartCount: Int,
    onToggleHindiMode: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenAdmin: () -> Unit,
    onSelectCategory: (ServiceCategory?) -> Unit,
    onOrderService: (StudioService) -> Unit,
    onAddToCart: (StudioService) -> Unit,
    onOpenStudioTab: () -> Unit
) {
    val context = LocalContext.current
    val services = (servicesState as? UiState.Success)?.data
        ?.takeIf { it.isNotEmpty() }
        ?: DefaultStudioSeed.starterServices().map { (id, svc) -> svc.copy(id = id) }

    val popularFrames = services.filter { it.categoryEnum == ServiceCategory.PHOTO_FRAMES }
        .sortedByDescending { it.popular }
        .take(6)

    val passportPackages = services.filter { it.categoryEnum == ServiceCategory.PASSPORT_PHOTO }
        .take(5)

    val photographyPackages = services.filter { it.categoryEnum == ServiceCategory.PHOTOGRAPHY_SERVICES }
        .take(5)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        border = BorderStroke(1.5.dp, ChampagneGold),
                        color = ObsidianBlack,
                        modifier = Modifier.size(44.dp)
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = businessConfig.businessName.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = WarmGold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (hindiMode) "फोटोग्राफी व प्रिंटिंग स्टूडियो" else "Photography & Print Studio",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSilver
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SlateVelvet,
                        border = BorderStroke(1.dp, CardBorderGold),
                        modifier = Modifier
                            .clickable(onClick = onToggleHindiMode)
                            .testTag("toggle_hindi_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Translate,
                                contentDescription = "Toggle Hindi-friendly labels",
                                tint = ChampagneGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (hindiMode) "EN+हिं" else "EN",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmGold
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenCart,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("home_cart_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(containerColor = ChampagneGold, contentColor = ObsidianBlack) {
                                        Text(cartCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingBag,
                                contentDescription = "Open Cart",
                                tint = IvoryCream
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenAdmin,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("home_admin_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AdminPanelSettings,
                            contentDescription = "Admin Dashboard",
                            tint = ChampagneGold
                        )
                    }
                }
            }
        }

        // 2. Welcome Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, CardBorderGold),
                colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Box(modifier = Modifier.height(250.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_studio),
                        contentDescription = "Studio Pixy Welcome Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        ObsidianBlack.copy(alpha = 0.25f),
                                        ObsidianBlack.copy(alpha = 0.78f),
                                        ObsidianBlack.copy(alpha = 0.96f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = ObsidianBlack.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = businessConfig.openingHours,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IvoryCream,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (hindiMode) {
                                    "आपकी खूबसूरत यादें, सोने जैसी चमक में"
                                } else {
                                    "Timeless Portraits & Bespoke Gold Frames"
                                },
                                style = MaterialTheme.typography.headlineLarge,
                                color = WarmGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = businessConfig.tagline,
                                style = MaterialTheme.typography.bodyMedium,
                                color = IvoryCream.copy(alpha = 0.9f),
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { onSelectCategory(null) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ChampagneGold,
                                        contentColor = ObsidianBlack
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("hero_explore_services_button")
                                ) {
                                    Text("Explore Services", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        IntentHelpers.openWhatsApp(
                                            context = context,
                                            phoneNumber = businessConfig.whatsappNumber,
                                            message = "Hello ${businessConfig.businessName}! I would like to inquire about your photography & printing services."
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = WhatsAppGreen,
                                        contentColor = ObsidianBlack
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("hero_whatsapp_order_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("WhatsApp", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Special Offer Banner
        if (businessConfig.offerDescription.isNotBlank()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, ChampagneGold),
                    colors = CardDefaults.cardColors(containerColor = SlateVelvet),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        BurnishedBronze.copy(alpha = 0.25f),
                                        SlateVelvet
                                    )
                                )
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ChampagneGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.LocalOffer,
                                    contentDescription = null,
                                    tint = WarmGold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = businessConfig.offerBadge.ifBlank { "SPECIAL OFFER" },
                                style = MaterialTheme.typography.labelMedium,
                                color = WarmGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = businessConfig.offerDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                color = IvoryCream
                            )
                        }
                    }
                }
            }
        }

        // 4. Our Services (4 Categories)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = if (hindiMode) "हमारी सेवाएं • Our Services" else "Our Services",
                    actionLabel = "All",
                    onActionClick = { onSelectCategory(null) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryGridTile(
                        title = ServiceCategory.PHOTO_FRAMES.displayName,
                        subtitle = if (hindiMode) ServiceCategory.PHOTO_FRAMES.hindiLabel else "8 Sizes & Custom",
                        icon = Icons.Filled.FilterFrames,
                        onClick = { onSelectCategory(ServiceCategory.PHOTO_FRAMES) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryGridTile(
                        title = ServiceCategory.PASSPORT_PHOTO.displayName,
                        subtitle = if (hindiMode) ServiceCategory.PASSPORT_PHOTO.hindiLabel else "5-Min Express",
                        icon = Icons.Filled.Badge,
                        onClick = { onSelectCategory(ServiceCategory.PASSPORT_PHOTO) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryGridTile(
                        title = ServiceCategory.PHOTOGRAPHY_SERVICES.displayName,
                        subtitle = if (hindiMode) ServiceCategory.PHOTOGRAPHY_SERVICES.hindiLabel else "Wedding & Events",
                        icon = Icons.Filled.CameraAlt,
                        onClick = { onSelectCategory(ServiceCategory.PHOTOGRAPHY_SERVICES) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryGridTile(
                        title = ServiceCategory.PHOTO_PRINTING.displayName,
                        subtitle = if (hindiMode) ServiceCategory.PHOTO_PRINTING.hindiLabel else "Prints & Restore",
                        icon = Icons.Filled.Print,
                        onClick = { onSelectCategory(ServiceCategory.PHOTO_PRINTING) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 5. Popular Photo Frames Carousel
        item {
            Column {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(
                        title = if (hindiMode) "लोकप्रिय फोटो फ्रेम • Photo Frames" else "Popular Photo Frames",
                        actionLabel = "View All",
                        onActionClick = { onSelectCategory(ServiceCategory.PHOTO_FRAMES) }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(popularFrames, key = { it.id.ifBlank { it.name } }) { frame ->
                        HorizontalHighlightCard(
                            service = frame,
                            hindiMode = hindiMode,
                            onOrderClick = { onOrderService(frame) },
                            onAddToCart = { onAddToCart(frame) }
                        )
                    }
                }
            }
        }

        // 6. Passport Photo Packages
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = if (hindiMode) "पासपोर्ट फोटो पैकेज • Passport Photos" else "Passport Photo Packages",
                    actionLabel = "All",
                    onActionClick = { onSelectCategory(ServiceCategory.PASSPORT_PHOTO) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    passportPackages.forEach { pkg ->
                        CompactPackageRow(
                            service = pkg,
                            hindiMode = hindiMode,
                            onOrderClick = { onOrderService(pkg) }
                        )
                    }
                }
            }
        }

        // 7. Photography Packages Carousel
        item {
            Column {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(
                        title = if (hindiMode) "फोटोग्राफी पैकेज • Photography" else "Photography Packages",
                        actionLabel = "View All",
                        onActionClick = { onSelectCategory(ServiceCategory.PHOTOGRAPHY_SERVICES) }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(photographyPackages, key = { it.id.ifBlank { it.name } }) { pkg ->
                        HorizontalHighlightCard(
                            service = pkg,
                            hindiMode = hindiMode,
                            onOrderClick = { onOrderService(pkg) },
                            onAddToCart = { onAddToCart(pkg) }
                        )
                    }
                }
            }
        }

        // 8. Contact Us Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
                border = BorderStroke(1.dp, CardBorderGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (hindiMode) "संपर्क करें • Contact Us" else "Contact Studio Pixy",
                        style = MaterialTheme.typography.headlineMedium,
                        color = WarmGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = businessConfig.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = IvoryCream
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Phone: ${businessConfig.phoneNumber}  •  ${businessConfig.email}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSilver
                    )
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
                                .testTag("home_call_button")
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call", maxLines = 1)
                        }

                        Button(
                            onClick = {
                                IntentHelpers.openWhatsApp(
                                    context,
                                    businessConfig.whatsappNumber,
                                    "Hello ${businessConfig.businessName}! I want to place an order."
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WhatsAppGreen,
                                contentColor = ObsidianBlack
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .minimumInteractiveComponentSize()
                                .testTag("home_contact_whatsapp_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp", fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = { IntentHelpers.openGoogleMaps(context, businessConfig.googleMapsQuery) },
                            border = BorderStroke(1.dp, ChampagneGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("home_maps_button")
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Maps", maxLines = 1)
                        }
                    }
                }
            }
        }

        // 9. Quick Studio Links (Gallery, Reviews, About, Share)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionPill(
                    icon = Icons.Filled.Collections,
                    label = "Gallery",
                    onClick = onOpenStudioTab,
                    modifier = Modifier.weight(1f)
                )
                QuickActionPill(
                    icon = Icons.Filled.Star,
                    label = "Reviews",
                    onClick = onOpenStudioTab,
                    modifier = Modifier.weight(1f)
                )
                QuickActionPill(
                    icon = Icons.Filled.Info,
                    label = "About",
                    onClick = onOpenStudioTab,
                    modifier = Modifier.weight(1f)
                )
                QuickActionPill(
                    icon = Icons.Filled.Share,
                    label = "Share",
                    onClick = {
                        IntentHelpers.shareStudioApp(
                            context,
                            businessConfig.businessName,
                            businessConfig.phoneNumber,
                            businessConfig.address
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = IvoryCream,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        TextButton(onClick = onActionClick) {
            Text(text = actionLabel, color = WarmGold)
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = WarmGold,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun CategoryGridTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
        border = BorderStroke(1.dp, CardBorderGold),
        modifier = modifier
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SlateVelvet,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = IvoryCream,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MutedSilver,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HorizontalHighlightCard(
    service: StudioService,
    hindiMode: Boolean,
    onOrderClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
        border = BorderStroke(1.dp, CardBorderGold),
        modifier = Modifier
            .width(210.dp)
            .clickable(onClick = onOrderClick)
    ) {
        Column {
            StudioServiceImage(
                imageKey = service.imageKey,
                contentDescription = service.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = IvoryCream,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (hindiMode && service.nameHindi.isNotBlank()) {
                    Text(
                        text = service.nameHindi,
                        style = MaterialTheme.typography.labelSmall,
                        color = WarmGold.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${IntentHelpers.formatInr(service.priceInr)} / ${service.unitLabel}",
                    style = MaterialTheme.typography.titleMedium,
                    color = WarmGold,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onAddToCart,
                        border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ Cart", style = MaterialTheme.typography.labelSmall)
                    }
                    Button(
                        onClick = onOrderClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WhatsAppGreen,
                            contentColor = ObsidianBlack
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Order", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactPackageRow(
    service: StudioService,
    hindiMode: Boolean,
    onOrderClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = AtelierCharcoal,
        border = BorderStroke(1.dp, CardBorderGold),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOrderClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = IvoryCream
                )
                Text(
                    text = if (hindiMode && service.nameHindi.isNotBlank()) {
                        "${service.nameHindi} • ${service.unitLabel}"
                    } else service.unitLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSilver
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = IntentHelpers.formatInr(service.priceInr),
                    style = MaterialTheme.typography.titleMedium,
                    color = WarmGold,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = WhatsAppGreen
                ) {
                    Text(
                        text = "Order",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = ObsidianBlack,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionPill(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SlateVelvet,
        border = BorderStroke(1.dp, CardBorderGold),
        modifier = modifier
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ChampagneGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = IvoryCream
            )
        }
    }
}
