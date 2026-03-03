package com.gorman.database.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gorman.common.models.CityData
import com.gorman.common.models.FiltersState

@Entity("settings")
data class SettingsEntity(
    @PrimaryKey(autoGenerate = false)
    val userId: String = "",
    val cityData: CityData? = null,
    val filtersState: FiltersState? = null
)
