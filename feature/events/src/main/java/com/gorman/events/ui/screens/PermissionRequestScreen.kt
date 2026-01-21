package com.gorman.events.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.events.R
import com.gorman.events.ui.components.CitySelectDropdownMenu
import com.gorman.ui.theme.LocalEventsMapTheme

@SuppressLint("ComposeModifierMissing")
@Composable
fun PermissionRequestScreen(
    showManualInput: Boolean,
    onCitySubmit: (CityCoordinatesConstants) -> Unit,
    shouldShowRationale: Boolean,
    requestPermissions: () -> Unit
) {
    var city by rememberSaveable { mutableStateOf<CityCoordinatesConstants?>(null) }
    val menuExpanded = rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(LocalEventsMapTheme.dimens.paddingExtraLarge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LocalEventsMapTheme.dimens.paddingLarge)
        ) {
            val text = when {
                showManualInput -> stringResource(R.string.declinedPermissions)
                shouldShowRationale -> stringResource(R.string.requestPermissions)
                else -> stringResource(R.string.defaultPermissionsText)
            }
            Header(text)
            if (showManualInput) {
                CitySelectDropdownMenu(
                    expanded = menuExpanded.value,
                    onExpandedChange = { menuExpanded.value = !menuExpanded.value },
                    onCityCheck = { city = it }
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
