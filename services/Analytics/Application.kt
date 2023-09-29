/*
netty server port - 50051
grpc server port  - 50052
 */

package com.weizen

import com.weizen.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 50051, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
    configureGrpcCloneRouting()
    configureGrpcServer()
}
