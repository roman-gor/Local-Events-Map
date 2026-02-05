package com.gorman.network.data.models

import kotlinx.serialization.Serializable

@Serializable
data class MapEventRemote(
    val id: String = "",
    val name: String? = null,
    val description: String? = null,
    val date: Long? = null,
    val city: String? = null,
    val address: String? = null,
    val coordinates: String? = null,
    val link: String? = null,
    val photo: String? = null,
    val category: String? = null,
    val price: Int? = null
)
