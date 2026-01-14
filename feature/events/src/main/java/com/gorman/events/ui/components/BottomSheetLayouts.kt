package com.gorman.events.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gorman.domain_model.MapEvent
import com.gorman.events.R
import com.gorman.events.ui.constants.CategoryConstants
import com.gorman.events.ui.screens.EventItem
import com.gorman.events.ui.states.FiltersState
import com.gorman.ui.theme.LocalEventsMapTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomEventsListSheetDialog(
    onDismiss: () -> Unit,
    selectedMapEvent: MapEvent?,
    onEventClick: (MapEvent) -> Unit,
    eventsList: List<MapEvent>,
    sheetState: SheetState
) {
    val configuration = LocalConfiguration.current
    val maxHeight = configuration.screenHeightDp.dp * 0.7f
    ModalBottomSheet(
        onDismissRequest = {onDismiss()},
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onSecondary,
        shape = RoundedCornerShape(topStart = LocalEventsMapTheme.dimens.cornerRadius,
            topEnd = LocalEventsMapTheme.dimens.cornerRadius),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(maxHeight)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            items(eventsList) { event ->
                EventItem(
                    mapEvent = event,
                    selectedMapEvent = selectedMapEvent,
                    onEventClick = onEventClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomFiltersSheetDialog(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    categoryItems: List<CategoryConstants>,
    filters: FiltersState,
    costItems: List<String>,
    onCategoryChange: (String) -> Unit,
    onDateRangeChange: () -> Unit,
    onDistanceChange: () -> Unit,
    onCostChange: () -> Unit,
    onNameChange: () -> Unit
) {
    val configuration = LocalConfiguration.current
    var categoryExpanded by remember { mutableStateOf(false) }
    val maxHeight = configuration.screenHeightDp.dp * 0.7f
    var dateRange by remember { mutableStateOf<Pair<Long, Long>>(Pair(0,0)) }
    var distance by remember { mutableStateOf(0) }
    var cost by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    Log.d("CategoriesList", categoryItems.toString())
    ModalBottomSheet(
        onDismissRequest = {onDismiss()},
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = LocalEventsMapTheme.dimens.cornerRadius,
            topEnd = LocalEventsMapTheme.dimens.cornerRadius),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(maxHeight),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomDropdownMenu(
                expanded = categoryExpanded,
                header = stringResource(R.string.category),
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                onItemClick = { onCategoryChange(it) },
                items = categoryItems,
                selectedItems = filters.categories
            )
        }
    }
}
