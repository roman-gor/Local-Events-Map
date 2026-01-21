package com.gorman.events.ui.states

import androidx.compose.runtime.Immutable
import com.gorman.common.constants.CategoryConstants
import com.gorman.events.ui.components.DateFilterType

@Immutable
data class FiltersState(
    val categories: List<String> = listOf(),
    val dateRange: DateFilterState = DateFilterState(),
    val distance: Int? = null,
    val isFree: Boolean = false,
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
    val onDistanceChange: (Int?) -> Unit,
    val onCostChange: (Boolean) -> Unit,
    val onNameChange: (String) -> Unit
)

@Immutable
data class FilterOptions(
    val categoryItems: List<CategoryConstants>,
    val costItems: List<String>
)
