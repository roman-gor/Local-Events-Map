package com.gorman.feature.details.impl.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.feature.details.impl.R
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun HeaderSection(
    name: String?,
    onNavigateToBack: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassyButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Arrow Back",
            onClick = onNavigateToBack,
            iconModifier = Modifier.size(24.dp),
            modifier = Modifier.clip(CircleShape).size(48.dp)
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = name ?: stringResource(R.string.event),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(
                    horizontal = LocalEventsMapTheme.dimens.paddingExtraLarge,
                    vertical = LocalEventsMapTheme.dimens.paddingMedium
                )
            )
        }
        GlassyButton(
            icon = Icons.Filled.Share,
            contentDescription = "Share",
            onClick = onShareClick,
            iconModifier = Modifier.size(24.dp),
            modifier = Modifier.clip(CircleShape).size(48.dp)
        )
    }
}
