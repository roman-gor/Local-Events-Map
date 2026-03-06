package com.gorman.database.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.gorman.common.constants.CityCoordinates

@Entity(
    tableName = "userCitySettings",
    foreignKeys = [
        ForeignKey(
            entity = UserDataEntity::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserCitySettingsEntity(
    @PrimaryKey val userId: String = "",
    val city: CityCoordinates? = null,
    val cityName: String? = city?.cityName,
    val latitude: Double? = null,
    val longitude: Double? = null
)
