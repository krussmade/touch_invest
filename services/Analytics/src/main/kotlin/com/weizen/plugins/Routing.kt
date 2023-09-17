package com.weizen.plugins

import com.weizen.service.AnalyticsService
import com.weizen.service.impl.AnalyticsServiceImpl
import io.ktor.server.application.*
import io.ktor.server.routing.*

/**
 * Configure routing for Netty server
 */
fun Application.configureRouting() {
    val service: AnalyticsService = AnalyticsServiceImpl()

    routing {
    }
}
