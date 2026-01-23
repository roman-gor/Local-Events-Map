package com.gorman.database.mappers

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StringListConverter {
    @TypeConverter
    fun fromList(list: List<String>): String =
        Json.encodeToString(list)

    @TypeConverter
    fun fromString(string: String): List<String> =
        Json.decodeFromString(string)
}
