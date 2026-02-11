package com.gorman.feature.events.impl.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.gorman.feature.events.impl.R
import com.gorman.ui.theme.LocalEventsMapTheme

@SuppressLint("ComposeModifierMissing")
@Composable
fun DateButtons(
    onFilterSelect: (DateFilterType) -> Unit,
    selectedFilterType: DateFilterType?,
    scrollState: ScrollState = rememberScrollState()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(LocalEventsMapTheme.dimens.paddingLarge))
        DateFilterType.entries.forEach { type ->
            DateButtonItem(
                type = type,
                isSelected = type == selectedFilterType,
                onClick = { onFilterSelect(type) },
            )
        }
        Spacer(Modifier.width(LocalEventsMapTheme.dimens.paddingLarge))
    }
}

@SuppressLint("ComposeModifierMissing")
@Composable
fun DateButtonItem(
    type: DateFilterType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = LocalEventsMapTheme.dimens.paddingSmall),
        shape = LocalEventsMapTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Text(
            text = stringResource(type.title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = if (isSelected) {
                MaterialTheme.colorScheme.inverseOnSurface
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(
                horizontal = LocalEventsMapTheme.dimens.paddingMedium,
                vertical = LocalEventsMapTheme.dimens.paddingSmall
            )
        )
    }
}

enum class DateFilterType(val title: Int) {
    TODAY(R.string.today),
    WEEK(R.string.week),
    RANGE(R.string.range)
}
