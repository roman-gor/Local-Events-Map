package com.gorman.events.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.events.R
import com.gorman.ui.theme.LocalEventsMapTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectDropdownMenu(
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onCityCheck: (CityCoordinatesConstants) -> Unit
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
            OutlinedTextField(
                value = selectedCity.value,
                onValueChange = {},
                placeholder = {
                    Text(
                        text = stringResource(R.string.labelCityText),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.surface)
                },
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                    focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange() },
                modifier = Modifier
                    .wrapContentHeight()
                    .exposedDropdownSize(),
                scrollState = rememberScrollState(),
                shape = RoundedCornerShape(LocalEventsMapTheme.dimens.cornerRadius),
                containerColor = MaterialTheme.colorScheme.background
            ) {
                CityCoordinatesConstants.cityCoordinatesList.forEach { city ->
                val cityName = CityNameDefinition(city)
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = cityName,
                                color = MaterialTheme.colorScheme.onSecondary,
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
