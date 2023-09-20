package com.weizen.service.impl

import com.weizen.service.AnalyticsService
import proto.Security.TAnalyticsGetPricesRangeRequest
import proto.Security.TAnalyticsGetPricesRangeResponse
import proto.Security.TSecurity
import proto.Security.TAnalyticsSendSecurityResponse
import proto.Security.TAnalyticsSendSecurityResponse.SecurityInsertResult
import proto.tAnalyticsSendSecurityResponse

class AnalyticsServiceImpl : AnalyticsService {
    override suspend fun sendSecurity(security: TSecurity): TAnalyticsSendSecurityResponse {
        // add to database
        println(security.time.toLocalDateTime())

        return tAnalyticsSendSecurityResponse {
            result = SecurityInsertResult.OK
            reason = "" // error msg or empty
        }
    }

    override suspend fun getPricesRange(request: TAnalyticsGetPricesRangeRequest): TAnalyticsGetPricesRangeResponse {
        // get from database

        // update from database
        val prices = listOf(0.0)

        // update from database
        val updateTimes = listOf(com.google.protobuf.Timestamp.newBuilder().setSeconds(0L).build())

        return TAnalyticsGetPricesRangeResponse.newBuilder()
            .addAllPrice(prices)
            .addAllUpdateTimes(updateTimes)
            .build()
    }
}