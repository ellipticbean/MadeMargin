package com.ellipticbean.mademargin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BurntOrange,
    onPrimary = WarmWhite,

    primaryContainer = SoftOrange,
    onPrimaryContainer = Espresso,

    secondary = Sage,
    onSecondary = WarmWhite,

    secondaryContainer = PaleSage,
    onSecondaryContainer = Espresso,

    background = Cream,
    onBackground = Espresso,

    surface = WarmWhite,
    onSurface = Espresso,

    surfaceVariant = SoftBeige,
    onSurfaceVariant = WarmGray,

    outline = SoftOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = LightOrange,
    onPrimary = DarkBackground,

    primaryContainer = DeepOrange,
    onPrimaryContainer = DarkText,

    secondary = LightSage,
    onSecondary = DarkBackground,

    secondaryContainer = Sage,
    onSecondaryContainer = DarkText,

    background = DarkBackground,
    onBackground = DarkText,

    surface = DarkSurface,
    onSurface = DarkText,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkMutedText,

    outline = DarkOutline
)

@Composable
fun MadeMarginTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}