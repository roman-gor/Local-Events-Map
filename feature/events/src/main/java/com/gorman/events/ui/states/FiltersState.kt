package com.gorman.events.ui.states

import com.gorman.common.constants.CategoryConstants

data class FiltersState(
    val categories: List<String> = listOf(),
    val dateRange: Pair<Long, Long> = Pair(0, 0),
    val distance: Int = 0,
    val isFree: Boolean = true,
    val name: String = ""
)

data class FilterActions(
    val onCategoryChange: (String) -> Unit,
    val onDateRangeChange: () -> Unit,
    val onDistanceChange: () -> Unit,
    val onCostChange: () -> Unit,
    val onNameChange: () -> Unit
)

data class FilterOptions(
    val categoryItems: List<CategoryConstants>,
    val costItems: List<String>
)
