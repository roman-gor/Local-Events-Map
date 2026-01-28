package com.gorman.feature.events.impl.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gorman.feature.events.impl.R
import com.gorman.ui.theme.LocalEventsMapTheme

@SuppressLint("ComposeModifierMissing")
@Composable
fun DistanceSwitch(
    isDistanceFilterEnabled: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(
            horizontal = LocalEventsMapTheme.dimens.paddingExtraLarge
        ),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = isDistanceFilterEnabled,
            onCheckedChange = { onCheckedChange() }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.distanceFilterAvailable),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
