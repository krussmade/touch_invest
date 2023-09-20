package com.weizen.service

import proto.Security.TAnalyticsGetPricesRangeRequest
import proto.Security.TAnalyticsGetPricesRangeResponse
import proto.Security.TAnalyticsSendSecurityResponse
import proto.Security.TSecurity
import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

interface AnalyticsService {
    fun Timestamp.toLocalDateTime(): LocalDateTime {
        val seconds = this.seconds
        return LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC)
    }

    suspend fun sendSecurity(security: TSecurity): TAnalyticsSendSecurityResponse

    suspend fun getPricesRange(request: TAnalyticsGetPricesRangeRequest): TAnalyticsGetPricesRangeResponse
}