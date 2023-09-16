/*
This server is configured in test purposes only
Do not use it in production!
Instead, mark your host and port in utils.AnalyticsClient
 */

package server

import io.grpc.ServerBuilder
import proto.AnalyticsGrpcKt
import proto.Security
import proto.tAnalyticsSendSecurityResponse

class AnalyticsService : AnalyticsGrpcKt.AnalyticsCoroutineImplBase() {
    override suspend fun sendSecurity(request: Security.TSecurity): Security.TAnalyticsSendSecurityResponse {
        println("SEC_ID: ${request.secId}   TRADE: ${request.boardId}   SYS_TIME: ${request.sysTime}")
        return tAnalyticsSendSecurityResponse {
            result = 0
        }
    }
}

fun main() {
    val port = 50051
    //prepare and run the gRPC web server
    val server = ServerBuilder
        .forPort(port)
        .addService(AnalyticsService())
        .build()
    server.start()
    //shutdown on application terminate
    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown()
    })
    //wait for connection until shutdown
    server.awaitTermination()
}