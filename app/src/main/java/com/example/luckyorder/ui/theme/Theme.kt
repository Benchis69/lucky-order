package com.example.luckyorder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen,
    background = BackgroundLight,
    surface = SurfaceWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen
    //  A light theme is enough for a cash register system
    // but the app won't crash if device is set to dark mode
)

@Composable
fun LuckyOrderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}