package com.gorman.feature.details.impl.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun GlassyButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onPrimary
) {
    IconButton(
        onClick = { onClick() },
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = iconModifier
        )
    }
}
