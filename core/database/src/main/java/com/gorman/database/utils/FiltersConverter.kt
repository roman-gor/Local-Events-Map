package com.gorman.database.utils

import androidx.room.TypeConverter
import com.gorman.common.models.FiltersState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FiltersConverter {
    @TypeConverter
    fun fromFilters(filters: FiltersState?): String? {
        return filters?.let { Json.encodeToString(filters) }
    }

    @TypeConverter
    fun toFilters(json: String?): FiltersState? {
        return json?.let { Json.decodeFromString<FiltersState?>(json) }
    }
}
