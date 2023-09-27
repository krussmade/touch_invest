package com.touchinves.features.operations

import com.touchinves.database.operations.OperationDTO
import com.touchinves.database.operations.OperationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class OperationController(
    private val call: ApplicationCall,
    private val operationService: OperationService
) {
    suspend fun createNewOperation() {
        val operationReceiveRemote = call.receive<OperationReceiveRemote>()

        val operationDTO = OperationDTO(
            id = null,
            portfolioId = operationReceiveRemote.portfolioId,
            type = operationReceiveRemote.type,
            secId = operationReceiveRemote.secId,
            price = operationReceiveRemote.price,
            qty = operationReceiveRemote.qty,
            date = operationReceiveRemote.date
        )

        val id = operationService.create(operationDTO)
        call.respond(HttpStatusCode.Created, id)
    }
}