package com.gorman.common.constants

enum class CostConstants(val value: String) {
    FREE("free"),
    PAID("paid");
    companion object {
        val costList = listOf(
            FREE, PAID
        )
    }
}
