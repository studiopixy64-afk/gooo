package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class ServiceCategory(
    val displayName: String,
    val hindiLabel: String
) {
    PHOTO_FRAMES("Photo Frames", "फोटो फ्रेम"),
    PASSPORT_PHOTO("Passport Photography", "पासपोर्ट फोटो"),
    PHOTOGRAPHY_SERVICES("Photography Services", "फोटोग्राफी सेवाएं"),
    PHOTO_PRINTING("Photo Printing", "फोटो प्रिंटिंग");

    companion object {
        fun fromRaw(raw: String?): ServiceCategory =
            entries.firstOrNull { it.name == raw } ?: PHOTO_FRAMES
    }
}

enum class OrderStatus(
    val displayName: String,
    val hindiLabel: String
) {
    PENDING("Pending", "प्रतीक्षारत"),
    CONFIRMED("Confirmed", "कन्फर्म"),
    COMPLETED("Completed", "पूर्ण");

    companion object {
        fun fromRaw(raw: String?): OrderStatus =
            entries.firstOrNull { it.name == raw } ?: PENDING
    }
}

@Keep
@IgnoreExtraProperties
data class StudioService(
    @DocumentId val id: String = "",
    val name: String = "",
    val nameHindi: String = "",
    val category: String = ServiceCategory.PHOTO_FRAMES.name,
    val priceInr: Long = 0L,
    val unitLabel: String = "per item",
    val description: String = "",
    val imageKey: String = "drawable:img_frames_showcase",
    val popular: Boolean = false,
    val active: Boolean = true,
    val sortOrder: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val categoryEnum: ServiceCategory
        get() = ServiceCategory.fromRaw(category)

    fun toCreateMap(): Map<String, Any> = mapOf(
        "name" to name.trim().take(80),
        "nameHindi" to nameHindi.trim().take(100),
        "category" to ServiceCategory.fromRaw(category).name,
        "priceInr" to priceInr.coerceIn(0L, 10_000_000L),
        "unitLabel" to unitLabel.trim().ifBlank { "per item" }.take(40),
        "description" to description.trim().take(400),
        "imageKey" to imageKey.take(250_000),
        "popular" to popular,
        "active" to active,
        "sortOrder" to sortOrder.coerceIn(0L, 10_000L),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun toUpdateMap(): Map<String, Any> = mapOf(
        "name" to name.trim().take(80),
        "nameHindi" to nameHindi.trim().take(100),
        "category" to ServiceCategory.fromRaw(category).name,
        "priceInr" to priceInr.coerceIn(0L, 10_000_000L),
        "unitLabel" to unitLabel.trim().ifBlank { "per item" }.take(40),
        "description" to description.trim().take(400),
        "imageKey" to imageKey.take(250_000),
        "popular" to popular,
        "active" to active,
        "sortOrder" to sortOrder.coerceIn(0L, 10_000L),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class BusinessConfig(
    @DocumentId val id: String = "studio_profile",
    val businessName: String = "Studio Pixy",
    val tagline: String = "Luxury Photography, Framing & Fine Prints",
    val whatsappNumber: String = "+919876543210",
    val phoneNumber: String = "+919876543210",
    val address: String = "Shop No. 12, Main Market Road, Near City Square, New Delhi 110001",
    val email: String = "studiopixy64@gmail.com",
    val openingHours: String = "Mon–Sat: 9:30 AM – 8:30 PM | Sun: 10:30 AM – 6:00 PM",
    val googleMapsQuery: String = "Studio Pixy Photography Studio, New Delhi",
    val offerBadge: String = "FESTIVE SPECIAL • 15% OFF",
    val offerDescription: String = "15% off on Wedding Photography & Custom 16x20 Gold Frames this season!",
    val aboutDescription: String = "Studio Pixy is your trusted neighbourhood photography studio and fine printing atelier. From crisp 5-minute passport & visa portraits to bespoke luxury frames, wedding cinematography, and heirloom photo restoration, we craft every memory with precision.",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "businessName" to businessName.trim().ifBlank { "Studio Pixy" }.take(60),
        "tagline" to tagline.trim().take(140),
        "whatsappNumber" to whatsappNumber.trim().take(20),
        "phoneNumber" to phoneNumber.trim().take(20),
        "address" to address.trim().take(240),
        "email" to email.trim().take(120),
        "openingHours" to openingHours.trim().take(160),
        "googleMapsQuery" to googleMapsQuery.trim().take(200),
        "offerBadge" to offerBadge.trim().take(60),
        "offerDescription" to offerDescription.trim().take(240),
        "aboutDescription" to aboutDescription.trim().take(1200),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun toUpdateMap(): Map<String, Any> = mapOf(
        "businessName" to businessName.trim().ifBlank { "Studio Pixy" }.take(60),
        "tagline" to tagline.trim().take(140),
        "whatsappNumber" to whatsappNumber.trim().take(20),
        "phoneNumber" to phoneNumber.trim().take(20),
        "address" to address.trim().take(240),
        "email" to email.trim().take(120),
        "openingHours" to openingHours.trim().take(160),
        "googleMapsQuery" to googleMapsQuery.trim().take(200),
        "offerBadge" to offerBadge.trim().take(60),
        "offerDescription" to offerDescription.trim().take(240),
        "aboutDescription" to aboutDescription.trim().take(1200),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class OrderLineItem(
    val serviceId: String = "",
    val serviceName: String = "",
    val unitPriceInr: Long = 0L,
    val quantity: Long = 1L,
    val lineTotalInr: Long = 0L
) {
    fun toMap(): Map<String, Any> = mapOf(
        "serviceId" to serviceId,
        "serviceName" to serviceName,
        "unitPriceInr" to unitPriceInr,
        "quantity" to quantity,
        "lineTotalInr" to lineTotalInr
    )
}

@Keep
@IgnoreExtraProperties
data class StudioOrder(
    @DocumentId val id: String = "",
    val userId: String = "",
    val customerEmail: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val requirements: String = "",
    val items: List<OrderLineItem> = emptyList(),
    val totalQuantity: Long = 1L,
    val totalAmountInr: Long = 0L,
    val status: String = OrderStatus.PENDING.name,
    val adminNotes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val statusEnum: OrderStatus
        get() = OrderStatus.fromRaw(status)
}

@Keep
@IgnoreExtraProperties
data class StudioReview(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val rating: Long = 5L,
    val serviceUsed: String = "",
    val comment: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CustomerProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val phone: String = "",
    val notes: String = "",
    val orderCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class CartItem(
    val service: StudioService,
    val quantity: Int = 1
) {
    val lineTotalInr: Long
        get() = service.priceInr * quantity.toLong()
}
