package com.weizen.service.impl

import com.weizen.service.AnalyticsService
import com.weizen.service.features.AnalyticHelper
import com.weizen.service.features.AnalyticSchema
import proto.Security.TAnalyticsGetPricesRangeRequest
import proto.Security.TAnalyticsGetPricesRangeResponse
import proto.Security.TSecurity
import proto.Security.TAnalyticsSendSecurityResponse
import proto.Security.TAnalyticsSendSecurityResponse.SecurityInsertResult
import proto.tAnalyticsSendSecurityResponse
import org.jetbrains.exposed.sql.*

class AnalyticsServiceImpl : AnalyticsService {
    val database = Database.connect(
        url = "jdbc:postgresql://rc1a-mt7pkzhjzvhc2iyi.mdb.yandexcloud.net:6432/db1",
        user = "user",
        driver = "org.postgresql.Driver",
        password = "hsese2023"
    )
    val analytics = AnalyticSchema(database)

    override suspend fun sendSecurity(security: TSecurity): TAnalyticsSendSecurityResponse {

        // security.updateTime.toLocalDateTime()

        return tAnalyticsSendSecurityResponse {
            result = SecurityInsertResult.OK
            reason = "" // error msg or empty
        }
    }

    override suspend fun getPricesRange(request: TAnalyticsGetPricesRangeRequest): TAnalyticsGetPricesRangeResponse {
        val beginPeriod = AnalyticHelper.timestampToInteger(request.beginPeriod)
        val endPeriod = AnalyticHelper.timestampToInteger(request.endPeriod)

        val prices = analytics.getListOfPrices(request.secId, beginPeriod, endPeriod)
        val updateTimes = analytics.getListOfTime(request.secId, beginPeriod, endPeriod)

        AnalyticHelper.calculateConfidenceInterval(prices)
        return TAnalyticsGetPricesRangeResponse.newBuilder()
            .addAllPrices(prices)
            .addAllUpdateTimes(updateTimes)
            .build()
    }
}



