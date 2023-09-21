package com.touchinves.database.operations

import kotlinx.serialization.Serializable
import java.sql.Date

@Serializable
data class OperationDTO(
    val id: Int?,
    val portfolioId: Int,
    val type: String,
    val secId: Int,
    val price: Int,
    val qty: Int,
    val date: String
)