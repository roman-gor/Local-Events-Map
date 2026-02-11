package com.gorman.feature.details.impl.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.feature.details.impl.R

@Composable
fun BottomSection(
    isLike: Boolean,
    onLikeClick: () -> Unit,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIcon = if (isLike) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassyButton(
            icon = selectedIcon,
            iconTint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Heart Icon",
            onClick = onLikeClick,
            iconModifier = Modifier.size(28.dp),
            modifier = Modifier.clip(CircleShape).size(58.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Button(
            onClick = onLinkClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(50),
            elevation = ButtonDefaults.elevatedButtonElevation(16.dp),
            modifier = Modifier.weight(1f).fillMaxHeight()
        ) {
            Text(
                text = stringResource(R.string.learnMore),
                color = MaterialTheme.colorScheme.onError,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
