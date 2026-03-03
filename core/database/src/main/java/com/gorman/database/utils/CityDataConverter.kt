package com.gorman.database.utils

import androidx.room.TypeConverter
import com.gorman.common.models.CityData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CityDataConverter {
    @TypeConverter
    fun fromCityData(cityData: CityData?): String? {
        return cityData?.let { Json.encodeToString(cityData) }
    }

    @TypeConverter
    fun toCityData(json: String?): CityData? {
        return json?.let { Json.decodeFromString<CityData?>(json) }
    }
}
