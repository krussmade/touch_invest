package com.touchinves.database.users

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int?,
    val login: String,
    val password: String,
    val telegram: String,
)
