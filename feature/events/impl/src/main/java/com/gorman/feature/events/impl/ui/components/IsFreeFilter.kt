package com.gorman.feature.events.impl.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
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
fun IsFreeFilter(
    isFree: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onCheckedChange(!isFree) })
            .padding(horizontal = LocalEventsMapTheme.dimens.paddingLarge),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = isFree,
            onCheckedChange = { onCheckedChange(it) }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.freeEventsOnly),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
