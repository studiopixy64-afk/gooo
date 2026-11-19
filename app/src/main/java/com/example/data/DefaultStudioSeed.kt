package com.example.data

import com.example.data.model.BusinessConfig
import com.example.data.model.ServiceCategory
import com.example.data.model.StudioService

object DefaultStudioSeed {

    const val ADMIN_EMAIL = "studiopixy64@gmail.com"
    const val DEFAULT_ADMIN_PASSCODE = "pixy2026"

    val defaultBusinessConfig = BusinessConfig()

    /**
     * Starter catalog template for all 27 requested Studio Pixy services.
     * Prices are editable anytime by the Admin in Firestore (/services/{id}).
     */
    fun starterServices(): List<Pair<String, StudioService>> = listOf(
        // A. PHOTO FRAMES
        "frame_4x6" to StudioService(
            name = "4x6 Photo Frame",
            nameHindi = "4x6 फोटो फ्रेम",
            category = ServiceCategory.PHOTO_FRAMES.name,
            priceInr = 250L,
            unitLabel = "per frame",
            description = "Sleek matte black & gold-edged tabletop frame with crystal glass.",
            imageKey = "drawable:img_frames_showcase",
            popular = true,
            active = true,
            sortOrder = 1L
        ),
        "frame_5x7" to StudioService(
            name = "5x7 Photo Frame",
            nameHindi = "5x7 फोटो फ्रेम",
            category = ServiceCategory.PHOTO_FRAMES.name,
            priceInr = 350L,
            unitLabel = "per frame",
            description = "Classic 5x7 portrait frame ideal for desk & gift displays.",
            imageKey = "drawable:img_frames_showcase",
            popular = false,
            active = true,
            sortOrder = 2L
        ),
        "frame_8x10" to StudioService(
            name = "8x10 Photo Frame",
            nameHindi = "8x10 फोटो फ्रेम",
            category = ServiceCategory.PHOTO_FRAMES.name,
            priceInr = 550L,
            unitLabel = "per frame",
            description = "Signature studio wall & tabletop frame with velvet backing.",
            imageKey = "drawable:img_frames_showcase",
            popular = true,
            active = true,
            sortOrder = 3L
        ),
        "frame_10x12" to StudioService(
            name = "10x12 Photo Frame",
            nameHindi = "10x12 फोटो फ्रेम",
            category = ServiceCategory.PHOTO_FRAMES.name,
            priceInr = 750L,
            unitLabel = "per frame",
            description = "Gallery-grade hardwood frame for family portraits.",
            imageKey = "drawable:img_frames_showcase",
            popular = false,
            active = true,
            sortOrder = 4L
        ),
        "frame_12x18" to StudioService(
            name = "12x18 Photo Frame",
            nameHindi = "12x18 फोटो फ्रेम",
            category = ServiceCategory.PHOTO_FRAMES.name,
            priceInr = 1200L,
            unitLabel = "per frame",
            description = "Large wall portrait frame with archival UV-safe mount.",
            imageKey = "drawable:img_frames_showcase",
            popular = true,
            active = true,
            sortOrder = 5L
        ),
        "frame_16x20" to StudioService(
            name = "16x20 Photo Frame",
            nameHindi = "16x20 फोटो फ्रेम",
            category = ServiceCategory.PHOTO_FRAMES.name,
            priceInr = 1850L,
            unitLabel = "per frame",
            description = "Brushed champagne gold statement frame for living rooms.",
            imageKey = "drawable:img_frames_showcase",
            popular = true,
            active = true,
            sortOrder = 6L
        ),
        "frame_20x24" to StudioService(
            name = "20x24 Photo Frame",
            nameHindi = "20x24 फोटो फ्रेम",
            category = ServiceCategory.PHOTO_FRAMES.name,
            priceInr = 2600L,
            unitLabel = "per frame",
            description = "Grand wedding & heirloom showcase frame with anti-glare acrylic.",
            imageKey = "drawable:img_frames_showcase",
            popular = false,
            active = true,
            sortOrder = 7L
        ),
        "frame_custom" to StudioService(
            name = "Custom Size Frames",
            nameHindi = "कस्टम साइज फोटो फ्रेम",
            category = ServiceCategory.PHOTO_FRAMES.name,
            priceInr = 900L,
            unitLabel = "starting price",
            description = "Bespoke dimensions, collage mats, and custom gold/teak moldings.",
            imageKey = "drawable:img_frames_showcase",
            popular = false,
            active = true,
            sortOrder = 8L
        ),

        // B. PASSPORT PHOTOGRAPHY
        "passport_standard" to StudioService(
            name = "Passport Size Photo",
            nameHindi = "पासपोर्ट साइज फोटो",
            category = ServiceCategory.PASSPORT_PHOTO.name,
            priceInr = 100L,
            unitLabel = "pack of 8 copies",
            description = "Crisp studio-lit passport portraits on premium lustre paper.",
            imageKey = "drawable:img_printing_studio",
            popular = true,
            active = true,
            sortOrder = 9L
        ),
        "passport_visa" to StudioService(
            name = "Visa Photo",
            nameHindi = "वीजा फोटो",
            category = ServiceCategory.PASSPORT_PHOTO.name,
            priceInr = 180L,
            unitLabel = "pack of 6 copies",
            description = "Embassy-compliant dimensions for US, UK, Schengen & Canada visas.",
            imageKey = "drawable:img_printing_studio",
            popular = true,
            active = true,
            sortOrder = 10L
        ),
        "passport_stamp" to StudioService(
            name = "Stamp Size Photo",
            nameHindi = "स्टाम्प साइज फोटो",
            category = ServiceCategory.PASSPORT_PHOTO.name,
            priceInr = 90L,
            unitLabel = "pack of 16 copies",
            description = "Mini ID & application form stamp-sized prints ready in 5 minutes.",
            imageKey = "drawable:img_printing_studio",
            popular = false,
            active = true,
            sortOrder = 11L
        ),
        "passport_digital" to StudioService(
            name = "Digital Passport Photo",
            nameHindi = "डिजिटल पासपोर्ट फोटो",
            category = ServiceCategory.PASSPORT_PHOTO.name,
            priceInr = 80L,
            unitLabel = "HD soft copy",
            description = "Background-cleaned, resized JPEG delivered on WhatsApp & Email.",
            imageKey = "drawable:img_printing_studio",
            popular = false,
            active = true,
            sortOrder = 12L
        ),
        "passport_urgent" to StudioService(
            name = "Urgent Passport Photo",
            nameHindi = "अर्जेंट पासपोर्ट फोटो",
            category = ServiceCategory.PASSPORT_PHOTO.name,
            priceInr = 150L,
            unitLabel = "5-min express",
            description = "Instant shoot, retouch & thermal dry print in under 5 minutes.",
            imageKey = "drawable:img_printing_studio",
            popular = true,
            active = true,
            sortOrder = 13L
        ),

        // C. PHOTOGRAPHY SERVICES
        "photo_wedding" to StudioService(
            name = "Wedding Photography",
            nameHindi = "वेडिंग फोटोग्राफी",
            category = ServiceCategory.PHOTOGRAPHY_SERVICES.name,
            priceInr = 25000L,
            unitLabel = "per event day",
            description = "Candid + traditional coverage, edited gallery & luxury wedding album.",
            imageKey = "drawable:img_wedding_shoot",
            popular = true,
            active = true,
            sortOrder = 14L
        ),
        "photo_birthday" to StudioService(
            name = "Birthday Photography",
            nameHindi = "बर्थडे फोटोग्राफी",
            category = ServiceCategory.PHOTOGRAPHY_SERVICES.name,
            priceInr = 4500L,
            unitLabel = "per party (4 hrs)",
            description = "Vibrant candid moments, cake-cutting coverage & edited digital album.",
            imageKey = "drawable:img_hero_studio",
            popular = false,
            active = true,
            sortOrder = 15L
        ),
        "photo_prewedding" to StudioService(
            name = "Pre-Wedding Photography",
            nameHindi = "प्री-वेडिंग फोटोग्राफी",
            category = ServiceCategory.PHOTOGRAPHY_SERVICES.name,
            priceInr = 15000L,
            unitLabel = "full day shoot",
            description = "Editorial couple shoot with lighting gear, styling & color grading.",
            imageKey = "drawable:img_wedding_shoot",
            popular = true,
            active = true,
            sortOrder = 16L
        ),
        "photo_event" to StudioService(
            name = "Event Photography",
            nameHindi = "इवेंट फोटोग्राफी",
            category = ServiceCategory.PHOTOGRAPHY_SERVICES.name,
            priceInr = 6500L,
            unitLabel = "per event",
            description = "Corporate events, engagements, anniversaries & stage coverage.",
            imageKey = "drawable:img_hero_studio",
            popular = false,
            active = true,
            sortOrder = 17L
        ),
        "photo_outdoor" to StudioService(
            name = "Outdoor Photography",
            nameHindi = "आउटडोर फोटोग्राफी",
            category = ServiceCategory.PHOTOGRAPHY_SERVICES.name,
            priceInr = 3500L,
            unitLabel = "2-hour session",
            description = "Golden hour outdoor portraits, maternity & portfolio shoots.",
            imageKey = "drawable:img_wedding_shoot",
            popular = false,
            active = true,
            sortOrder = 18L
        ),
        "photo_family" to StudioService(
            name = "Family Photography",
            nameHindi = "फैमिली फोटोग्राफी",
            category = ServiceCategory.PHOTOGRAPHY_SERVICES.name,
            priceInr = 2500L,
            unitLabel = "studio session",
            description = "Warm multi-generation studio portrait session with 2 framed prints.",
            imageKey = "drawable:img_hero_studio",
            popular = false,
            active = true,
            sortOrder = 19L
        ),
        "photo_cinematic" to StudioService(
            name = "Cinematic Video Shooting",
            nameHindi = "सिनेमैटिक वीडियो शूटिंग",
            category = ServiceCategory.PHOTOGRAPHY_SERVICES.name,
            priceInr = 18000L,
            unitLabel = "per event",
            description = "4K gimbal cinematography, drone highlights & emotional teaser film.",
            imageKey = "drawable:img_wedding_shoot",
            popular = true,
            active = true,
            sortOrder = 20L
        ),

        // D. PHOTO PRINTING
        "print_4x6" to StudioService(
            name = "4x6 Photo Printing",
            nameHindi = "4x6 फोटो प्रिंटिंग",
            category = ServiceCategory.PHOTO_PRINTING.name,
            priceInr = 20L,
            unitLabel = "per print",
            description = "Rich color glossy or matte lab print from phone or camera files.",
            imageKey = "drawable:img_printing_studio",
            popular = true,
            active = true,
            sortOrder = 21L
        ),
        "print_5x7" to StudioService(
            name = "5x7 Photo Printing",
            nameHindi = "5x7 फोटो प्रिंटिंग",
            category = ServiceCategory.PHOTO_PRINTING.name,
            priceInr = 40L,
            unitLabel = "per print",
            description = "True-tone lab print on heavyweight archival photo paper.",
            imageKey = "drawable:img_printing_studio",
            popular = false,
            active = true,
            sortOrder = 22L
        ),
        "print_8x10" to StudioService(
            name = "8x10 Photo Printing",
            nameHindi = "8x10 फोटो प्रिंटिंग",
            category = ServiceCategory.PHOTO_PRINTING.name,
            priceInr = 100L,
            unitLabel = "per print",
            description = "Exhibition-grade portrait print with zero fading guarantee.",
            imageKey = "drawable:img_printing_studio",
            popular = false,
            active = true,
            sortOrder = 23L
        ),
        "print_a4" to StudioService(
            name = "A4 Photo Printing",
            nameHindi = "A4 फोटो प्रिंटिंग",
            category = ServiceCategory.PHOTO_PRINTING.name,
            priceInr = 120L,
            unitLabel = "per sheet",
            description = "Borderless A4 studio color printing on silk or metallic finish.",
            imageKey = "drawable:img_printing_studio",
            popular = true,
            active = true,
            sortOrder = 24L
        ),
        "print_a3" to StudioService(
            name = "A3 Photo Printing",
            nameHindi = "A3 फोटो प्रिंटिंग",
            category = ServiceCategory.PHOTO_PRINTING.name,
            priceInr = 280L,
            unitLabel = "per sheet",
            description = "Large format poster & portrait print with vivid pigment inks.",
            imageKey = "drawable:img_printing_studio",
            popular = false,
            active = true,
            sortOrder = 25L
        ),
        "print_enlargement" to StudioService(
            name = "Photo Enlargement",
            nameHindi = "फोटो एनलार्जमेंट (बड़ा प्रिंट)",
            category = ServiceCategory.PHOTO_PRINTING.name,
            priceInr = 450L,
            unitLabel = "starting price",
            description = "AI-assisted high-DPI upscaling & wall-size fine art printing.",
            imageKey = "drawable:img_printing_studio",
            popular = false,
            active = true,
            sortOrder = 26L
        ),
        "print_restoration" to StudioService(
            name = "Old Photo Restoration",
            nameHindi = "पुरानी फोटो रिस्टोरेशन",
            category = ServiceCategory.PHOTO_PRINTING.name,
            priceInr = 350L,
            unitLabel = "per photo",
            description = "Repair scratches, tears, fading & optional colorization of vintage photos.",
            imageKey = "drawable:img_printing_studio",
            popular = true,
            active = true,
            sortOrder = 27L
        )
    )
}
