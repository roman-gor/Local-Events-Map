package com.gorman.events.ui.states

import androidx.compose.runtime.Immutable
import com.gorman.common.constants.CategoryConstants
import com.gorman.events.ui.components.DateFilterType

@Immutable
data class FiltersState(
    val categories: List<String> = listOf(),
    val dateRange: DateFilterState = DateFilterState(),
    val distance: Int = 0,
    val isFree: Boolean = true,
    val name: String = ""
)

@Immutable
data class DateFilterState(
    val type: DateFilterType? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)

@Immutable
data class FilterActions(
    val onCategoryChange: (String) -> Unit,
    val onDateRangeChange: (DateFilterState) -> Unit,
    val onDistanceChange: () -> Unit,
    val onCostChange: () -> Unit,
    val onNameChange: (String) -> Unit
)

@Immutable
data class FilterOptions(
    val categoryItems: List<CategoryConstants>,
    val costItems: List<String>
)
