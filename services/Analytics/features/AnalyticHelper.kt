package com.weizen.service.features

import kotlin.math.sqrt
import com.google.protobuf.Timestamp

object AnalyticHelper {
    fun calculateConfidenceInterval(prices: List<Double>): Pair<Double, Double> {
        val mean = prices.average()
        val standardDeviation = sqrt(prices.map { it -> Math.pow(it - mean, 2.0) }.sum() / prices.size)

        // Таблицное значение Z-оценки для доверительного интервала 95%
        val z = 1.96
        val marginOfError = z * (standardDeviation / sqrt(prices.size.toDouble()))

        val lowerBound = mean - marginOfError
        val upperBound = mean + marginOfError
        return Pair(lowerBound, upperBound)
    }

    fun timestampToInteger(timestamp: Timestamp): Int {
        return timestamp.seconds.toInt()
    }

    fun integerToTimestamp(integer: Int): Timestamp {
        return Timestamp.newBuilder().setSeconds(integer.toLong()).build()
    }
}



