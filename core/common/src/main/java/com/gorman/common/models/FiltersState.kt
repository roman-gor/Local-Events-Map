package com.gorman.common.models

import androidx.compose.runtime.Immutable
import com.gorman.common.R
import com.gorman.common.constants.Category

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
    val onNameChange: (String) -> Unit,
    val onResetFilters: () -> Unit,
)

@Immutable
data class FilterOptions(
    val categoryItems: List<Category>,
    val costItems: List<String>
)

enum class DateFilterType(val title: Int) {
    TODAY(R.string.today),
    WEEK(R.string.week),
    RANGE(R.string.range)
}
