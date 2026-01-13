package com.gorman.common

data class Event (
    val localId: Int = 0,
    val remoteId: String = "",
    val name: String? = null,
    val description: String? = null,
    val address: String? = null,
    val coordinates: String,
    val ling: String? = null,
    val photo: String? = null,
    val category: String? = null,
    val price: String? = null,
    val isFavourite: Boolean = false
)
