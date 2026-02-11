package com.gorman.database.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("events")
data class MapEventEntity(
    @PrimaryKey val id: String = "",
    val name: String? = null,
    val description: String? = null,
    val city: String? = null,
    val date: Long? = null,
    val address: String? = null,
    val coordinates: String? = null,
    val link: String? = null,
    val photo: String? = null,
    val category: String? = null,
    val price: Int? = null,
    val isFavourite: Boolean? = null
)
