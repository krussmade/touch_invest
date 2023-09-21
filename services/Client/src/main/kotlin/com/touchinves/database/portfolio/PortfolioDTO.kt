package com.touchinves.database.portfolio

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioDTO(
    val id: Int?,
    val ownerId: Int,
    val name: String
)