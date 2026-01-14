package com.gorman.database.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("events")
data class MapEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: String = "",
    val name: String? = null,
    val description: String? = null,
    val address: String? = null,
    val coordinates: String,
    val link: String? = null,
    val photo: String? = null,
    val category: String? = null,
    val price: Int? = null,
    val isFavourite: Boolean = false
)
