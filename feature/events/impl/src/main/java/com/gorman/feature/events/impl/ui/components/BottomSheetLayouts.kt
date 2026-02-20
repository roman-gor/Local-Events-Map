package com.gorman.feature.events.impl.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.common.models.DateFilterState
import com.gorman.common.models.FilterActions
import com.gorman.common.models.FilterOptions
import com.gorman.common.models.FiltersState
import com.gorman.feature.events.impl.R
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme
import kotlinx.collections.immutable.ImmutableList

@SuppressLint("ConfigurationScreenWidthHeight", "ComposeModifierMissing")
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
        Text(
            text = stringResource(R.string.events),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LocalEventsMapTheme.dimens.paddingLarge)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            itemsIndexed(eventsList) { index, event ->
                MapEventItem(
                    mapEvent = event,
                    onEventClick = onEventClick
                )
                if (index != eventsList.size - 1) {
                    Spacer(
                        modifier = Modifier.height(1.dp).fillMaxWidth().background(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight", "ComposeModifierMissing")
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
    val isDistanceFilterEnabled = filters.distance != null
    var isDistanceChange by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background.copy(alpha = alpha.floatValue),
        shape = RoundedCornerShape(
            topStart = LocalEventsMapTheme.dimens.cornerRadius,
            topEnd = LocalEventsMapTheme.dimens.cornerRadius
        ),
        modifier = Modifier.fillMaxSize().systemBarsPadding()
    ) {
        if (!isDistanceChange) {
            Header(
                text = stringResource(R.string.filters),
                onResetFiltersClick = { actions.onResetFilters() },
                modifier = Modifier.fillMaxWidth().padding(horizontal = LocalEventsMapTheme.dimens.paddingLarge)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FiltersBottomSheetContent(
                data = FilterBottomSheetData(
                    isDistanceChange = isDistanceChange,
                    isDistanceFilterEnabled = isDistanceFilterEnabled,
                    categoryExpanded = categoryExpanded,
                    actions = actions,
                    options = options,
                    filters = filters,
                    onDistanceValueChanged = { value ->
                        alpha.floatValue = 0f
                        isDistanceChange = true
                        actions.onDistanceChange(value.toInt())
                    },
                    onDistanceValueChangeFinished = {
                        alpha.floatValue = 1f
                        isDistanceChange = false
                    },
                    onDistanceFilterEnabled = {
                        if (isDistanceFilterEnabled) {
                            actions.onDistanceChange(null)
                        } else {
                            actions.onDistanceChange(1)
                        }
                    },
                    onDateFilterSelect = { actions.onDateRangeChange(it) },
                    onCategoriesItemClick = { actions.onCategoryChange(it) },
                    onCategoriesListExpanded = { categoryExpanded = !categoryExpanded },
                    onNameChanged = { actions.onNameChange(it) }
                )
            )
        }
    }
}

@SuppressLint("ComposeMultipleContentEmitters", "ComposeModifierMissing")
@Composable
fun FiltersBottomSheetContent(
    data: FilterBottomSheetData
) {
    val localNameState = remember(data.filters.name) { mutableStateOf(data.filters.name) }
    if (!data.isDistanceChange) {
        Spacer(modifier = Modifier.height(LocalEventsMapTheme.dimens.paddingSmall))
        MapEventNameOutlineTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = LocalEventsMapTheme.dimens.paddingLarge),
            currentName = localNameState.value,
            onNameChanged = {
                localNameState.value = it
                data.onNameChanged(it)
            }
        )
        Spacer(modifier = Modifier.height(LocalEventsMapTheme.dimens.paddingSmall))
        CategoriesDropdownMenu(
            expanded = data.categoryExpanded,
            header = stringResource(R.string.category),
            onExpandedChange = { data.onCategoriesListExpanded() },
            onItemClick = { data.actions.onCategoryChange(it) },
            categoriesOptions = CategoriesOptions(
                items = data.options.categoryItems,
                selectedItems = data.filters.categories
            )
        )
        Spacer(modifier = Modifier.height(LocalEventsMapTheme.dimens.paddingSmall))
        DateButtons(
            onFilterSelect = {
                data.onDateFilterSelect(DateFilterState(type = it))
            },
            selectedFilterType = data.filters.dateRange.type
        )
        Spacer(modifier = Modifier.height(LocalEventsMapTheme.dimens.paddingLarge))
    } else {
        Spacer(modifier = Modifier.height(100.dp))
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!data.isDistanceChange) {
            DistanceSwitch(data.isDistanceFilterEnabled) {
                data.onDistanceFilterEnabled()
            }
        }
        data.filters.distance?.let {
            DistanceSlider(
                distance = it,
                onValueChange = { value ->
                    data.onDistanceValueChanged(value)
                },
                onValueChangeFinished = {
                    data.onDistanceValueChangeFinished()
                },
                enabled = data.isDistanceFilterEnabled
            )
        }
    }
    Spacer(modifier = Modifier.height(LocalEventsMapTheme.dimens.paddingMedium))
    if (!data.isDistanceChange) {
        IsFreeFilter(
            isFree = data.filters.isFree,
            onCheckedChange = { data.actions.onCostChange(it) }
        )
    }
}

@Composable
fun Header(
    text: String,
    onResetFiltersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { onResetFiltersClick() },
            modifier = Modifier
                .background(Color.Transparent)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_clean_filter),
                contentDescription = "Reset Filters Icon",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Immutable
data class FilterBottomSheetData(
    val isDistanceChange: Boolean,
    val isDistanceFilterEnabled: Boolean,
    val categoryExpanded: Boolean,
    val actions: FilterActions,
    val options: FilterOptions,
    val filters: FiltersState,
    val onNameChanged: (String) -> Unit,
    val onDistanceValueChanged: (Float) -> Unit,
    val onDistanceValueChangeFinished: () -> Unit,
    val onDistanceFilterEnabled: () -> Unit,
    val onDateFilterSelect: (DateFilterState) -> Unit,
    val onCategoriesItemClick: (String) -> Unit,
    val onCategoriesListExpanded: () -> Unit
)
