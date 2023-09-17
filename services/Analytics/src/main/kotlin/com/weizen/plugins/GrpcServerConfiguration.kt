package com.weizen.plugins

import com.weizen.grpc.GrpcServer
import com.weizen.service.AnalyticsService
import com.weizen.service.impl.AnalyticsServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import kotlinx.coroutines.launch
import proto.Security

/**
 * Launch Grpc server as a separate coroutine
 */
fun Application.configureGrpcServer(port: Int = 50052) {
    launch {
        log.info("Starting Grpc server at port $port")
        GrpcServer(port).startGrpcServer()
    }.invokeOnCompletion {
        log.info("Grpc server stopped successfully")
    }
}

/**
 * Clones of grpc functions
 * in order to substitute grpc server
 */
fun Application.configureGrpcCloneRouting() {
    val service: AnalyticsService = AnalyticsServiceImpl()

    routing {
        post("security/send") {
            val readChannel = call.receiveChannel().toByteArray()
            val security = Security.TSecurity.parseFrom(readChannel)

            val serviceResponse = service.sendSecurity(security)
            val status = when (serviceResponse.result) {
                Security.TAnalyticsSendSecurityResponse.SecurityInsertResult.OK -> HttpStatusCode.Created
                Security.TAnalyticsSendSecurityResponse.SecurityInsertResult.ALREADY_EXISTS -> HttpStatusCode.Found
                Security.TAnalyticsSendSecurityResponse.SecurityInsertResult.DB_FAIL -> HttpStatusCode.InternalServerError
                Security.TAnalyticsSendSecurityResponse.SecurityInsertResult.UNKNOWN_FAIL -> HttpStatusCode.InternalServerError
                else -> HttpStatusCode.InternalServerError
            }

            call.respondText(text = serviceResponse.reason, status = status)
        }
    }
}