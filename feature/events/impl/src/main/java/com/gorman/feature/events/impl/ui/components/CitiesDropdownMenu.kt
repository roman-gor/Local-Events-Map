package com.gorman.feature.events.impl.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.common.constants.CityCoordinates
import com.gorman.ui.theme.LocalEventsMapTheme
import kotlinx.collections.immutable.ImmutableList

@SuppressLint("ComposeModifierMissing")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitiesDropdownMenu(
    expanded: Boolean,
    currentCity: String,
    onExpandedChange: () -> Unit,
    onCityClick: (CityCoordinates) -> Unit,
    citiesList: ImmutableList<CityCoordinates>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
            .background(color = Color.Transparent),
        contentAlignment = Alignment.TopCenter
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange() },
            modifier = Modifier.wrapContentWidth()
        ) {
            CityDropdownHeader(
                currentCity = currentCity,
                expanded = expanded,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                        shape = LocalEventsMapTheme.shapes.small
                    )
                    .padding(
                        vertical = LocalEventsMapTheme.dimens.paddingMedium,
                        horizontal = LocalEventsMapTheme.dimens.paddingExtraLarge
                    )
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .wrapContentWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange() },
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                shape = LocalEventsMapTheme.shapes.small,
                modifier = Modifier.wrapContentWidth().exposedDropdownSize(),
            ) {
                citiesList.forEach { city ->
                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CityUiItem(
                                    city = city,
                                    isSelected = stringResource(city.resource) == currentCity
                                )
                            }
                        },
                        onClick = {
                            onCityClick(city)
                            onExpandedChange()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

@Composable
fun CityDropdownHeader(
    currentCity: String,
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.wrapContentWidth()
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentCity,
                maxLines = 1,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .rotate(if (expanded) 180f else 0f)
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@SuppressLint("ComposeModifierMissing")
@Composable
fun CityUiItem(
    city: CityCoordinates,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Check Icon"
            )
        }
        Text(
            text = stringResource(city.resource),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
