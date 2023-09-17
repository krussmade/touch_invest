package com.weizen.service

import proto.Security.TAnalyticsSendSecurityResponse
import proto.Security.TSecurity

interface AnalyticsService {
    suspend fun sendSecurity(security: TSecurity): TAnalyticsSendSecurityResponse
}