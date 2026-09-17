package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = SaffronPrimaryDark,
    onPrimary = Color(0xFF431B00),
    primaryContainer = SaffronDark,
    onPrimaryContainer = Color(0xFFFFDBC9),
    secondary = TempleGoldDark,
    onSecondary = Color(0xFF402D00),
    secondaryContainer = Color(0xFF5B4200),
    onSecondaryContainer = Color(0xFFFFDF9B),
    tertiary = PeacockTeal,
    background = SandalwoodDarkBackground,
    surface = SandalwoodDarkSurface,
    onBackground = Color(0xFFEDE0D6),
    onSurface = Color(0xFFEDE0D6),
    surfaceVariant = Color(0xFF282420),
    onSurfaceVariant = Color(0xFFCCC4BE)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SaffronPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBC9),
    onPrimaryContainer = Color(0xFF371200),
    secondary = TempleGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDF9B),
    onSecondaryContainer = Color(0xFF261900),
    tertiary = PeacockTeal,
    onTertiary = Color.White,
    background = HeritageParchment,
    surface = SandalwoodCream,
    onBackground = ImperialNavy,
    onSurface = ImperialNavy,
    surfaceVariant = SandalwoodSurface,
    onSurfaceVariant = DeepCharcoal,
  )

@Composable
fun BharatHeritageTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve our distinctive heritage colors
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

