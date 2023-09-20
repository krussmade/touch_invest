/*
gRPC server for Analytics
 */

package com.weizen.grpc

import com.weizen.service.AnalyticsService
import com.weizen.service.impl.AnalyticsServiceImpl
import io.grpc.ServerBuilder
import proto.AnalyticsGrpcKt
import proto.Security.TAnalyticsGetPricesRangeRequest
import proto.Security.TAnalyticsGetPricesRangeResponse
import proto.Security.TAnalyticsSendSecurityResponse
import proto.Security.TSecurity

class AnalyticsDispatcher : AnalyticsGrpcKt.AnalyticsCoroutineImplBase() {
    val service: AnalyticsService = AnalyticsServiceImpl()

    override suspend fun sendSecurity(request: TSecurity): TAnalyticsSendSecurityResponse {
        return service.sendSecurity(request)
    }

    override suspend fun getPricesRange(request: TAnalyticsGetPricesRangeRequest): TAnalyticsGetPricesRangeResponse {
        return service.getPricesRange(request)
    }
}

class GrpcServer(private val port: Int) {

    private val server = ServerBuilder
        .forPort(port)
        .addService(AnalyticsDispatcher())
        .build()

    fun startGrpcServer() {
        server.start()

        //shutdown on application terminate
        Runtime.getRuntime().addShutdownHook(Thread {
            server.shutdown()
        })
        //wait for connection until shutdown
        server.awaitTermination()
    }
}