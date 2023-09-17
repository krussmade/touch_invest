package com.weizen.service.impl

import com.weizen.service.AnalyticsService
import proto.Security.TSecurity
import proto.Security.TAnalyticsSendSecurityResponse
import proto.Security.TAnalyticsSendSecurityResponse.SecurityInsertResult
import proto.tAnalyticsSendSecurityResponse

class AnalyticsServiceImpl : AnalyticsService {
    override suspend fun sendSecurity(security: TSecurity): TAnalyticsSendSecurityResponse {
        // add to database

        return tAnalyticsSendSecurityResponse {
            result = SecurityInsertResult.OK
            reason = "" // error msg or empty
        }
    }
}