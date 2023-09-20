package utils

import kotlinx.serialization.json.*
import proto.Security.TSecurity
import proto.tSecurity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private fun String.toSecondsWithDate(): Long {
    val clear = this.replace("\"", "")
    return LocalDateTime.parse(clear, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        .toEpochSecond(ZoneOffset.UTC)
}

private fun String.toSeconds(): Long {
    val clear = this.replace("\"", "")
    val currentDate = LocalDate.now()
    return LocalTime.parse(clear, DateTimeFormatter.ofPattern("HH:mm:ss"))
        .toEpochSecond(currentDate, ZoneOffset.UTC)
}

private fun String.toGoogleTime(): com.google.protobuf.Timestamp {
    return com.google.protobuf.timestamp {
        seconds = try {
            this@toGoogleTime.toSeconds()
        } catch (e: DateTimeParseException) {
            try {
                this@toGoogleTime.toSecondsWithDate()
            } catch (e: DateTimeParseException) {
                0L
            }
        }
    }
}

private fun JsonElement.unpackString(): String {
    return this.jsonPrimitive.content
}

private fun JsonElement.unpackDouble(): Double {
    return this.jsonPrimitive.doubleOrNull ?: 0.0
}

private fun JsonElement.unpackLong(): Long {
    return this.jsonPrimitive.longOrNull ?: 0L
}

private fun JsonElement.unpackInt(): Int {
    return this.jsonPrimitive.intOrNull ?: 0
}

private fun JsonElement.unpackGoogleTime(): com.google.protobuf.Timestamp {
    return this.jsonPrimitive.content.toGoogleTime()
}

internal fun convertToSecurityProto(data: JsonArray): TSecurity {
    return tSecurity {
        secId = data[0].unpackString()
        boardId = data[1].unpackString()
        bId = data[2].unpackDouble()
        // reserved 3;
        offer = data[4].unpackDouble()
        // reserved 5;
        spread = data[6].unpackDouble()
        bIdDepthT = data[7].unpackInt()
        offerDepthT = data[8].unpackInt()
        open = data[9].unpackDouble()
        low = data[10].unpackDouble()
        high = data[11].unpackDouble()
        last = data[12].unpackDouble()
        lastChange = data[13].unpackDouble()
        lastChangePrcnt = data[14].unpackDouble()
        qty = data[15].unpackInt()
        value = data[16].unpackDouble()
        valueUsd = data[17].unpackDouble()
        waPrice = data[18].unpackDouble()
        lastCngToLastWaPrice = data[19].unpackDouble()
        waPToPrevWaPricePrcnt = data[20].unpackDouble()
        waPToPrevWaPrice = data[21].unpackDouble()
        closePrice = data[22].unpackDouble()
        marketPriceToday = data[23].unpackDouble()
        marketPrice = data[24].unpackDouble()
        lastToPrevPrice = data[25].unpackDouble()
        numTrades = data[26].unpackInt()
        volToday = data[27].unpackLong()
        valToday = data[28].unpackLong()
        valTodayUsd = data[29].unpackLong()
        etfSettlePrice = data[30].unpackDouble()
        tradingStatus = data[31].unpackString()
        updateTime = data[32].unpackGoogleTime()
        // reserved 33;
        // reserved 34;
        lClosePrice = data[35].unpackDouble()
        lCurrentPrice = data[36].unpackDouble()
        marketPrice2 = data[37].unpackDouble()
        // reserved 38
        // reserved 39
        change = data[40].unpackDouble()
        time = data[41].unpackGoogleTime()
        // reserved 42
        // reserved 43
        priceMinusPrevWaPrice = data[44].unpackDouble()
        openPeriodPrice = data[45].unpackDouble()
        seqNum = data[46].unpackLong()
        sysTime = data[47].unpackGoogleTime()
        closingAuctionPrice = data[48].unpackDouble()
        closingAuctionVolume = data[49].unpackDouble()
        issueCapitalization = data[50].unpackDouble()
        issueCapitalizationUpdateTime = data[51].unpackGoogleTime()
        etfSettleCurrency = data[52].unpackString()
        valTodayRur = data[53].unpackLong()
        tradingSession = data[54].unpackString()
    }
}
