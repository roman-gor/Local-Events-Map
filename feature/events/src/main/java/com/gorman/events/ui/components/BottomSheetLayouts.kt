package com.gorman.events.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gorman.events.R
import com.gorman.events.ui.screens.EventItem
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
            .fillMaxSize().systemBarsPadding()
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
    var dateRange by remember { mutableStateOf<Pair<Long, Long>>(Pair(0, 0)) }
    var distance by remember { mutableIntStateOf(0) }
    var cost by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    Log.d("CategoriesList", options.categoryItems.toString())
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(
            topStart = LocalEventsMapTheme.dimens.cornerRadius,
            topEnd = LocalEventsMapTheme.dimens.cornerRadius
        ),
        modifier = Modifier
            .fillMaxSize().systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
        }
    }
}
