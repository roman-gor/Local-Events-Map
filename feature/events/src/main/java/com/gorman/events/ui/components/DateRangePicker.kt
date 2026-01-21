package com.gorman.events.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.events.R
import com.gorman.events.ui.states.DateFilterState
import com.gorman.ui.theme.LocalEventsMapTheme

@SuppressLint("ComposeModifierMissing")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onDateRangeSelected: (DateFilterState) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        DateFilterState(
                            type = DateFilterType.RANGE,
                            startDate = dateRangePickerState.selectedStartDateMillis,
                            endDate = dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dismiss))
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = stringResource(R.string.selectDateRange),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(
                        horizontal = LocalEventsMapTheme.dimens.paddingLarge,
                        vertical = LocalEventsMapTheme.dimens.paddingSmall
                    )
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(
                    vertical = LocalEventsMapTheme.dimens.paddingLarge
                )
        )
    }
}
