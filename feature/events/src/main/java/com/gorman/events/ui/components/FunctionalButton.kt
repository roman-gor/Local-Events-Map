package com.gorman.events.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FunctionalButton(
    onClick: () -> Unit,
    iconSize: Dp,
    imageVector: ImageVector,
    modifier: Modifier
) {
    Button(
        onClick = { onClick() },
        shape = CircleShape,
        modifier = modifier,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 12.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "list_button",
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun FunctionalButton(
    onClick: () -> Unit,
    iconSize: Dp,
    painter: Painter,
    modifier: Modifier
) {
    Button(
        onClick = { onClick() },
        shape = CircleShape,
        modifier = modifier,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 12.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            painter = painter,
            contentDescription = "list_button",
            modifier = Modifier.size(iconSize)
        )
    }
}
