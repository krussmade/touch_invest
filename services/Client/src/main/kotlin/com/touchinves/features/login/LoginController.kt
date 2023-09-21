package com.touchinves.features.login

import com.touchinves.database.users.UserDTO
import com.touchinves.database.users.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.playzone.features.login.LoginReceiveRemote
import ru.playzone.features.register.RegisterReceiveRemote

class LoginController(
    private val call: ApplicationCall,
    private val userService: UserService
) {
    suspend fun performLogin() {
        val loginReceiveRemote = call.receive<LoginReceiveRemote>()
        val userDTO = userService.read(loginReceiveRemote.login)

        if (userDTO == null) {
            call.respond(HttpStatusCode.Conflict, "User not found")
        } else {
            if (userDTO.password == loginReceiveRemote.password){
                call.respond(HttpStatusCode.Created, userDTO)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}