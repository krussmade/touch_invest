package com.weizen.service.features

import com.google.type.DateTime

data class AnalyticDTO(
    val secId: String,
    val boardId: String,
    val open: Double,
    val low: Double,
    val high: Double,
    val last: Double,
    val updateTime: Int,
    val time: Int
)
