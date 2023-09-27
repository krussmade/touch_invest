package com.touchinves.clients

import com.google.protobuf.timestamp
import io.grpc.ManagedChannelBuilder
import proto.AnalyticsGrpcKt
import proto.Security.TAnalyticsGetLastPriceResponse
import proto.Security.TAnalyticsGetPricesRangeResponse
import proto.tAnalyticsGetLastPriceRequest
import proto.tAnalyticsGetPricesRangeRequest
import java.time.LocalDateTime
import java.time.ZoneOffset

class AnalyticsClient {
    private val host = "analytics"
    private val portGRPC = 50052

    private val channel = ManagedChannelBuilder.forAddress(host, portGRPC).usePlaintext().build()
    private val stub = AnalyticsGrpcKt.AnalyticsCoroutineStub(channel)

    // bring to core utils
    private fun LocalDateTime.toGoogleTimestamp(): com.google.protobuf.Timestamp {
        return timestamp {
            seconds = this@toGoogleTimestamp.toEpochSecond(ZoneOffset.UTC)
        }
    }

    suspend fun getLastPrice(tSecId: String, tBoardId: String = "TQBR"): TAnalyticsGetLastPriceResponse {
        val request = tAnalyticsGetLastPriceRequest {
            secId = tSecId
            boardId = tBoardId
        }
        return stub.getLastPrice(request)
    }

    suspend fun getPricesRange(
        tBeginPeriod: LocalDateTime, tEndPeriod: LocalDateTime, tSecId: String,
        tBoardId: String = "TQBR"
    ): TAnalyticsGetPricesRangeResponse {
        val request = tAnalyticsGetPricesRangeRequest {
            beginPeriod = tBeginPeriod.toGoogleTimestamp()
            endPeriod = tEndPeriod.toGoogleTimestamp()
            secId = tSecId
            boardId = tBoardId
        }
        return stub.getPricesRange(request)
    }
}