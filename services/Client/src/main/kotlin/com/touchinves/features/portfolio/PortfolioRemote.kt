package com.touchinves.features.portfolio

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioReceiveRemote(
    val ownerId: Int,
    val name: String
)