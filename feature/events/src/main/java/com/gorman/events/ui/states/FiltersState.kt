package com.gorman.events.ui.states

data class FiltersState(
    val categories: List<String> = listOf(),
    val dateRange: Pair<Long, Long> = Pair(0, 0),
    val distance: Int = 0,
    val isFree: Boolean = true,
    val name: String = ""
)
