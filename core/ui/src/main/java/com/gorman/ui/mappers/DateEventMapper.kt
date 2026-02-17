package com.gorman.ui.mappers

import com.gorman.ui.states.DateEventUiModel
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.utils.DateFormatStyle
import com.gorman.ui.utils.format

fun MapUiEvent.toDateUiModel(): DateEventUiModel {
    val localDate = date

    return if (localDate != null) {
        DateEventUiModel(
            day = localDate.format(DateFormatStyle.DAY_ONLY),
            month = localDate.format(DateFormatStyle.MONTH_ONLY),
            dayOfWeek = localDate.format(DateFormatStyle.DAY_WEEK_FULL)
                .replaceFirstChar { it.titlecase() },
            time = localDate.format(DateFormatStyle.TIME_ONLY)
        )
    } else {
        DateEventUiModel("", "", "", "")
    }
}
