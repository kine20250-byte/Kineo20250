package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = JudicialGold,
    secondary = CourtGreenLight,
    tertiary = JudicialGoldDark,
    background = CourtDarkBackground,
    surface = CourtDarkSurface,
    onPrimary = OnParchmentText,
    onSecondary = WhitePure,
    onBackground = NeutralParchment,
    onSurface = NeutralParchment
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CourtGreen,
    secondary = JudicialGold,
    tertiary = CourtGreenLight,
    background = NeutralParchment,
    surface = WhitePure,
    onPrimary = WhitePure,
    onSecondary = OnParchmentText,
    onBackground = OnParchmentText,
    onSurface = OnParchmentText
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // نقوم بتعطيل الألوان الديناميكية لإجبار الألوان الرسمية العريقة لمحاكم اليمن
  dynamicColor: Boolean = false,
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
