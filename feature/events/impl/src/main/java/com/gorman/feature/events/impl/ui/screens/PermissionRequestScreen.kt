package com.gorman.feature.events.impl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.common.constants.CityCoordinates
import com.gorman.feature.events.impl.R
import com.gorman.feature.events.impl.ui.components.CitySelectDropdownMenu
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun PermissionRequestScreen(
    showManualInput: Boolean,
    onCitySubmit: (CityCoordinates) -> Unit,
    shouldShowRationale: Boolean,
    requestPermissions: () -> Unit,
    modifier: Modifier = Modifier,
    isPreRequest: Boolean = false,
    onDeclineClick: () -> Unit = {}
) {
    var city by rememberSaveable { mutableStateOf<CityCoordinates?>(null) }
    val menuExpanded = rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LocalEventsMapTheme.dimens.paddingLarge)
        ) {
            val text = when {
                isPreRequest -> stringResource(R.string.preRequestPermissionExplanation)
                showManualInput -> stringResource(R.string.declinedPermissions)
                shouldShowRationale -> stringResource(R.string.requestPermissions)
                else -> stringResource(R.string.defaultPermissionsText)
            }
            Header(text)
            if (isPreRequest) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = onDeclineClick
                    ) {
                        Text(stringResource(R.string.decline))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = requestPermissions
                    ) {
                        Text(stringResource(R.string.approve))
                    }
                }
            } else if (showManualInput) {
                CitySelectDropdownMenu(
                    expanded = menuExpanded.value,
                    onExpandedChange = { menuExpanded.value = !menuExpanded.value },
                    onCityCheck = { city = it },
                    modifier = Modifier.wrapContentHeight()
                )
                Button(
                    onClick = { city?.let { onCitySubmit(it) } },
                    enabled = city != null
                ) {
                    Text(
                        text = stringResource(R.string.findCityEventsText),
                        fontSize = 14.sp
                    )
                }
            } else {
                Button(
                    onClick = requestPermissions
                ) {
                    Text(
                        text = stringResource(R.string.providePermissions),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        textAlign = TextAlign.Center
    )
}
