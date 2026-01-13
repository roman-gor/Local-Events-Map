package com.gorman.firebase.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventFirebase(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val date: String = "",
    val address: String = "",
    val coordinates: String = "",
    val link: String = "",
    val photo: String = "",
    val category: String = "",
    val price: Int = 0
)
