package com.gorman.network.data.models

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class MapEventRemote(
    @SerialName("id")
    val id: String = "",
    @SerialName("name")
    val name: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("date")
    val date: Long? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("address")
    val address: String? = null,
    @SerialName("coordinates")
    val coordinates: String? = null,
    @SerialName("link")
    val link: String? = null,
    @SerialName("photo")
    val photo: String? = null,
    @SerialName("category")
    val category: String? = null,
    @SerialName("price")
    val price: Int? = null
)
