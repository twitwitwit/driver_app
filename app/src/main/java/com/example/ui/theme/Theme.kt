package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SwiftGold,
    onPrimary = SwiftDark,
    primaryContainer = SwiftGoldLight,
    onPrimaryContainer = SwiftDark,
    secondary = SwiftDark,
    onSecondary = SwiftWhite,
    secondaryContainer = SwiftDarkCard,
    onSecondaryContainer = SwiftWhite,
    tertiary = SwiftGreen,
    background = SwiftGrayBg,
    onBackground = SwiftTextPrimary,
    surface = SwiftWhite,
    onSurface = SwiftTextPrimary,
    surfaceVariant = SwiftBorderLight,
    onSurfaceVariant = SwiftTextSecondary,
    outline = SwiftBorder,
    error = SwiftRed,
    onError = SwiftWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = SwiftGold,
    onPrimary = SwiftDark,
    primaryContainer = SwiftDarkCard,
    onPrimaryContainer = SwiftGoldLight,
    secondary = SwiftGold,
    onSecondary = SwiftDark,
    background = SwiftDark,
    onBackground = SwiftWhite,
    surface = SwiftDarkSurface,
    onSurface = SwiftWhite,
    surfaceVariant = SwiftDarkCard,
    onSurfaceVariant = SwiftTextMuted,
    outline = SwiftDarkCard,
    error = SwiftRed,
    onError = SwiftWhite
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to crisp light theme matching presentation
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = Color.Transparent.toArgb()
                it.navigationBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(it, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        // Disable patterned ripples which crash/spam on non-hardware accelerated canvases
        CompositionLocalProvider(
            LocalRippleConfiguration provides RippleConfiguration(
                color = colorScheme.primary,
                rippleAlpha = androidx.compose.material3.RippleDefaults.RippleAlpha
            ),
            content = content
        )
    }
}
