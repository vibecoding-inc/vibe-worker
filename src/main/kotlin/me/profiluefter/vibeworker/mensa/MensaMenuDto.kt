package me.profiluefter.vibeworker.mensa

import java.time.LocalDate

data class MensaMenuDto(
    val restaurantId: Long,
    val restaurantName: String,
    val date: LocalDate,
    val dishes: List<MensaDishDto>
)

data class MensaDishDto(
    val name: String,
    val price: Double?,
    val category: String?
)
