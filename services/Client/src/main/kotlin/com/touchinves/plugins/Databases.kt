package com.touchinves.plugins

import com.touchinves.database.operations.OperationService
import com.touchinves.database.portfolio.PortfolioService
import com.touchinves.database.users.UserDTO
import com.touchinves.database.users.UserService
import com.touchinves.features.login.LoginController
import com.touchinves.features.operations.OperationController
import com.touchinves.features.portfolio.PortfolioController
import com.touchinves.features.register.RegisterController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:postgresql://rc1a-mt7pkzhjzvhc2iyi.mdb.yandexcloud.net:6432/db1",
        user = "user",
        driver = "org.postgresql.Driver",
        password = "hsese2023"
    )
    val userService = UserService(database)
    val portfolioService = PortfolioService(database)
    val operationService = OperationService(database)

    routing {
        post("/login") {
            val loginController = LoginController(call, userService)
            loginController.performLogin()
        }

        post("/register") {
            val registerController = RegisterController(call, userService)
            registerController.registerNewUser()
        }

        post("/portfolio") {
            val portfolioController = PortfolioController(call, portfolioService)
            portfolioController.createNewPortfolio()
        }

        post("/operation") {
            val operationController = OperationController(call, operationService)
            operationController.createNewOperation()
        }
    }
}
