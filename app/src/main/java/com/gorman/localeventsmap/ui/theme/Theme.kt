package com.gorman.localeventsmap.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.gorman.ui.theme.Dimens
import com.gorman.ui.theme.LocalDimens
import com.gorman.ui.theme.LocalEventsMapTheme.dimens

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

object LocalEventsMapTheme {
    val dimens: Dimens
        @Composable
        get() = LocalDimens.current
}

@Composable
fun LocalEventsMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    CompositionLocalProvider(
        LocalDimens provides dimens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
