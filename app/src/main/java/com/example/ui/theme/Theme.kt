package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val StudioPixyDarkColorScheme = darkColorScheme(
    primary = ChampagneGold,
    onPrimary = ObsidianBlack,
    primaryContainer = BurnishedBronze,
    onPrimaryContainer = IvoryCream,
    secondary = WarmGold,
    onSecondary = ObsidianBlack,
    secondaryContainer = SlateVelvet,
    onSecondaryContainer = WarmGold,
    tertiary = WhatsAppGreen,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = IvoryCream,
    surface = AtelierCharcoal,
    onSurface = IvoryCream,
    surfaceVariant = SlateVelvet,
    onSurfaceVariant = MutedSilver,
    outline = CardBorderGold,
    outlineVariant = SubtleDivider,
    error = CrimsonAlert,
    onError = IvoryCream
)

val StudioPixyShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun StudioPixyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StudioPixyDarkColorScheme,
        typography = StudioPixyTypography,
        shapes = StudioPixyShapes,
        content = content
    )
}
