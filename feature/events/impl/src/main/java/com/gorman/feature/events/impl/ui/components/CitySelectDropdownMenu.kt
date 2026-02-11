package com.gorman.feature.events.impl.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gorman.common.constants.CityCoordinates
import com.gorman.feature.events.impl.R
import com.gorman.ui.theme.LocalEventsMapTheme

@SuppressLint("ComposeModifierMissing")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectDropdownMenu(
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onCityCheck: (CityCoordinates) -> Unit
) {
    val selectedCity = rememberSaveable { mutableStateOf("") }
    Box(
        modifier = Modifier.wrapContentHeight(),
        contentAlignment = Alignment.TopCenter
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LocalEventsMapTheme.dimens.paddingLarge)
        ) {
            CityTextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                selectedCity = selectedCity.value,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange() },
                modifier = Modifier
                    .wrapContentHeight()
                    .exposedDropdownSize(),
                scrollState = rememberScrollState(),
                shape = LocalEventsMapTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                CityCoordinates.entries.forEach { city ->
                    val cityName = stringResource(city.resource)
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = cityName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = LocalEventsMapTheme.dimens.paddingMedium)
                            )
                        },
                        onClick = {
                            selectedCity.value = cityName
                            onExpandedChange()
                            onCityCheck(city)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CityTextField(
    selectedCity: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable () -> Unit
) {
    OutlinedTextField(
        value = selectedCity,
        onValueChange = {},
        placeholder = {
            Text(
                text = stringResource(R.string.labelCityText),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        },
        readOnly = true,
        modifier = modifier,
        trailingIcon = { trailingIcon() },
        shape = LocalEventsMapTheme.shapes.medium
    )
}
