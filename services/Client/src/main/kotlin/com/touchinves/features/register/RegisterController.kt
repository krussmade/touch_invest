package com.touchinves.features.register

import com.touchinves.database.users.UserDTO
import com.touchinves.database.users.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.playzone.features.register.RegisterReceiveRemote
import java.util.*

class RegisterController(
    private val call: ApplicationCall,
    private val userService: UserService
) {
    suspend fun registerNewUser() {
        val registerReceiveRemote = call.receive<RegisterReceiveRemote>()

        val userDTO = UserDTO(
            id = null,
            login = registerReceiveRemote.login,
            password = registerReceiveRemote.password,
            telegram = registerReceiveRemote.telegram
        )

        if (userService.read(registerReceiveRemote.login) != null) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
        } else {
            try {
                val id = userService.create(userDTO)
                call.respond(HttpStatusCode.Created, id)
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.Conflict, "User already exists")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Can't create user ${e.localizedMessage}")
            }
        }
    }
}