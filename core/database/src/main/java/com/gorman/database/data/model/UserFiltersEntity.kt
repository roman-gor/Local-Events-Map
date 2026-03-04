package com.gorman.database.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.gorman.common.models.DateFilterType

@Entity(
    tableName = "userFilters",
    foreignKeys = [
        ForeignKey(
            entity = UserDataEntity::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserFiltersEntity(
    @PrimaryKey(autoGenerate = false)
    val userId: String = "",
    val categories: List<String> = listOf(),
    val type: DateFilterType? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val distance: Int? = null,
    val isFree: Boolean = false,
    val name: String = ""
)
