package com.gorman.ui.utils

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

    return remember(density, systemBarInsets) {
        val systemBottomPx = systemBarInsets.getBottom(density)
        val barHeightPx = with(density) { 100.dp.roundToPx() }

        val totalPx = systemBottomPx + barHeightPx

        with(density) { totalPx.toDp() }
    }
}
