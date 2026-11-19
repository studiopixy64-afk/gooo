package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import com.example.data.model.CartItem
import com.example.data.model.StudioOrder
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.util.Locale

object IntentHelpers {

    fun formatInr(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        return "₹${formatter.format(amount)}"
    }

    fun cleanWhatsAppDigits(rawNumber: String): String {
        val digits = rawNumber.filter { it.isDigit() }
        return when {
            digits.length == 10 -> "91$digits"
            digits.startsWith("0") && digits.length == 11 -> "91${digits.drop(1)}"
            else -> digits
        }
    }

    fun buildSingleOrCartOrderMessage(
        businessName: String,
        orderId: String?,
        customerName: String,
        customerPhone: String,
        requirements: String,
        items: List<CartItem>
    ): String {
        val total = items.sumOf { it.lineTotalInr }
        return buildString {
            appendLine("*$businessName — New Order*")
            if (!orderId.isNullOrBlank()) {
                appendLine("Order ID: #$orderId")
            }
            appendLine("------------------------")
            items.forEachIndexed { idx, item ->
                appendLine("${idx + 1}. ${item.service.name} × ${item.quantity} = ${formatInr(item.lineTotalInr)}")
            }
            appendLine("------------------------")
            appendLine("*Estimated Total:* ${formatInr(total)}")
            appendLine("*Customer:* $customerName")
            appendLine("*Phone:* $customerPhone")
            if (requirements.isNotBlank()) {
                appendLine("*Requirements:* $requirements")
            }
            append("Sent via Studio Pixy Android App")
        }
    }

    fun buildExistingOrderMessage(businessName: String, order: StudioOrder): String {
        return buildString {
            appendLine("*$businessName — Order #${order.id}*")
            appendLine("Status: ${order.statusEnum.displayName}")
            appendLine("------------------------")
            order.items.forEachIndexed { idx, item ->
                appendLine("${idx + 1}. ${item.serviceName} × ${item.quantity} = ${formatInr(item.lineTotalInr)}")
            }
            appendLine("------------------------")
            appendLine("*Estimated Total:* ${formatInr(order.totalAmountInr)}")
            appendLine("*Customer:* ${order.customerName} (${order.customerPhone})")
            if (order.requirements.isNotBlank()) {
                appendLine("*Requirements:* ${order.requirements}")
            }
        }
    }

    fun openWhatsApp(context: Context, phoneNumber: String, message: String) {
        val digits = cleanWhatsAppDigits(phoneNumber)
        if (digits.isBlank()) {
            Toast.makeText(context, "Studio WhatsApp number not configured", Toast.LENGTH_SHORT).show()
            return
        }
        val uri = Uri.parse("https://wa.me/$digits?text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to open WhatsApp or browser", Toast.LENGTH_SHORT).show()
        }
    }

    fun dialPhone(context: Context, phoneNumber: String) {
        val cleaned = phoneNumber.trim()
        if (cleaned.isBlank()) return
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleaned")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "No phone dialer available", Toast.LENGTH_SHORT).show()
        }
    }

    fun openGoogleMaps(context: Context, query: String) {
        val encoded = Uri.encode(query.ifBlank { "Studio Pixy" })
        val geoIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$encoded")).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(geoIntent)
        } catch (_: Exception) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=$encoded")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(webIntent)
            } catch (_: Exception) {
                Toast.makeText(context, "Unable to open Maps", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendEmail(context: Context, email: String, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${email.trim()}")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareStudioApp(context: Context, businessName: String, phone: String, address: String) {
        val text = buildString {
            appendLine("Check out *$businessName* — Photography, Custom Photo Frames, Passport Photos & Printing!")
            appendLine("Address: $address")
            appendLine("Call / WhatsApp: $phone")
        }
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, businessName)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(sendIntent, "Share $businessName").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(chooser)
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to share right now", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Compresses a Photo Picker URI into a small JPEG data URI (<= ~100KB) for Firestore storage.
     */
    fun uriToCompressedDataUrl(context: Context, uri: Uri): String? {
        return try {
            val resolver = context.contentResolver
            val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, boundsOpts)
            }
            val maxDim = 480
            var sampleSize = 1
            while (boundsOpts.outWidth / sampleSize > maxDim || boundsOpts.outHeight / sampleSize > maxDim) {
                sampleSize *= 2
            }
            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val decoded = resolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOpts)
            } ?: return null

            val ratio = minOf(
                maxDim.toFloat() / decoded.width.coerceAtLeast(1),
                maxDim.toFloat() / decoded.height.coerceAtLeast(1),
                1f
            )
            val scaled = if (ratio < 1f) {
                Bitmap.createScaledBitmap(
                    decoded,
                    (decoded.width * ratio).toInt().coerceAtLeast(1),
                    (decoded.height * ratio).toInt().coerceAtLeast(1),
                    true
                )
            } else {
                decoded
            }

            val out = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 72, out)
            val bytes = out.toByteArray()
            if (scaled != decoded) scaled.recycle()
            decoded.recycle()

            if (bytes.size > 180_000) return null
            "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (_: Exception) {
            null
        }
    }
}
