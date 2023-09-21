package com.touchinves.features.portfolio

import com.touchinves.database.portfolio.PortfolioDTO
import com.touchinves.database.portfolio.PortfolioService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class PortfolioController(
    private val call: ApplicationCall,
    private val portfolioService: PortfolioService
) {
    suspend fun createNewPortfolio() {
        val portfolioReceiveRemote = call.receive<PortfolioReceiveRemote>()

        val portfolioDTO = PortfolioDTO(
            id = null,
            ownerId = portfolioReceiveRemote.ownerId,
            name = portfolioReceiveRemote.name
        )

        val id = portfolioService.create(portfolioDTO)
        call.respond(HttpStatusCode.Created, id)
    }
}