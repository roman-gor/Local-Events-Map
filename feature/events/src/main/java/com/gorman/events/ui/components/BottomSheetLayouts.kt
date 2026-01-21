package com.gorman.events.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gorman.events.R
import com.gorman.events.ui.screens.EventItem
import com.gorman.events.ui.states.DateFilterState
import com.gorman.events.ui.states.FilterActions
import com.gorman.events.ui.states.FilterOptions
import com.gorman.events.ui.states.FiltersState
import com.gorman.events.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme
import kotlinx.collections.immutable.ImmutableList

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapEventsBottomSheet(
    onDismiss: () -> Unit,
    onEventClick: (MapUiEvent) -> Unit,
    eventsList: ImmutableList<MapUiEvent>,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(
            topStart = LocalEventsMapTheme.dimens.cornerRadius,
            topEnd = LocalEventsMapTheme.dimens.cornerRadius
        ),
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            items(eventsList) { event ->
                EventItem(
                    mapEvent = event,
                    onEventClick = onEventClick
                )
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    filters: FiltersState,
    options: FilterOptions,
    actions: FilterActions
) {
    var categoryExpanded by remember { mutableStateOf(false) }
    val alpha = remember { mutableFloatStateOf(1f) }
    var isDistanceFilterEnabled by remember { mutableStateOf(filters.distance != null) }
    var isDistanceChange by remember { mutableStateOf(false) }
    Log.d("CategoriesList", options.categoryItems.toString())
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background.copy(alpha = alpha.value),
        shape = RoundedCornerShape(
            topStart = LocalEventsMapTheme.dimens.cornerRadius,
            topEnd = LocalEventsMapTheme.dimens.cornerRadius
        ),
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isDistanceChange) {
                CategoriesDropdownMenu(
                    expanded = categoryExpanded,
                    header = stringResource(R.string.category),
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    onItemClick = { actions.onCategoryChange(it) },
                    categoriesOptions = CategoriesOptions(
                        items = options.categoryItems,
                        selectedItems = filters.categories
                    )
                )
                Spacer(modifier = Modifier.height(LocalEventsMapTheme.dimens.paddingSmall))
                DateButtons(
                    onFilterSelect = {
                        actions.onDateRangeChange(DateFilterState(type = it))
                    },
                    selectedFilterType = filters.dateRange.type
                )
                Spacer(modifier = Modifier.height(LocalEventsMapTheme.dimens.paddingLarge))
            } else {
                Spacer(modifier = Modifier.height(100.dp))
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isDistanceChange) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(
                            horizontal = LocalEventsMapTheme.dimens.paddingExtraLarge
                        ),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = isDistanceFilterEnabled,
                            onCheckedChange = {
                                isDistanceFilterEnabled = !isDistanceFilterEnabled
                                if (!isDistanceFilterEnabled) {
                                    actions.onDistanceChange(null)
                                } else {
                                    actions.onDistanceChange(1)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.distanceFilterAvailable),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                filters.distance?.let {
                    DistanceSlider(
                        distance = it,
                        onValueChange = { value ->
                            alpha.value = 0f
                            isDistanceChange = true
                            actions.onDistanceChange(value.toInt())
                        },
                        onValueChangeFinished = {
                            isDistanceChange = false
                            alpha.value = 1f
                        },
                        enabled = isDistanceFilterEnabled
                    )
                }
            }
            Spacer(modifier = Modifier.height(LocalEventsMapTheme.dimens.paddingMedium))
            if (!isDistanceChange) {
                IsFreeFilter(
                    isFree = filters.isFree,
                    onCheckedChange = { actions.onCostChange(it) }
                )
            }
        }
    }
}
