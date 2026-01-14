package com.gorman.localeventsmap.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.gorman.ui.theme.Black
import com.gorman.ui.theme.Blue80
import com.gorman.ui.theme.LightGray
import com.gorman.ui.theme.LightGray40
import com.gorman.ui.theme.LightGray80
import com.gorman.ui.theme.White

private val DarkColorScheme = darkColorScheme(
    background = Black,
    primary = LightGray,
    secondary = LightGray80,
    onSecondary = White,
    surface = LightGray40,
    onSurface = Blue80
)

private val LightColorScheme = lightColorScheme(
    background = White,
    primary = LightGray,
    secondary = LightGray40,
    onSecondary = Black,
    surface = LightGray80,
    onSurface = Blue80
)

@Composable
fun LocalEventsMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
