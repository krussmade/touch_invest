package com.touchinves.features.operations

import kotlinx.serialization.Serializable

@Serializable
data class OperationReceiveRemote(
    val portfolioId: Int,
    val type: String,
    val secId: Int,
    val price: Int,
    val qty: Int,
    val date: String
)
