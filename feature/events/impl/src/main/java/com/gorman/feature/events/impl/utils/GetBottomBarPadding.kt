package com.gorman.feature.events.impl.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun getBottomBarPadding(): Dp {
    val density = LocalDensity.current
    val systemBarInsets = WindowInsets.navigationBars

    val bottomPaddingPx = remember(density, systemBarInsets) {
        val systemBottom = systemBarInsets.getBottom(density)

        val barHeightPx = with(density) { 32.dp.roundToPx() }

        systemBottom + barHeightPx
    }.dp
    return bottomPaddingPx
}
