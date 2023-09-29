package com.weizen.service.features

import com.google.type.DateTime
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*

class AnalyticSchema(private val database: Database) {
    object Analytic : Table() {
        val secId =  varchar("sec_id", length = 50)
        val boardId =  varchar("board_id", length = 50)
        val open =  double("open")
        val low =  double("low")
        val high =  double("high")
        val last =  double("last")
        val updateTime =  integer("update_time")
        val time =  integer("time")

        override val primaryKey = PrimaryKey(secId)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Analytic)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(analyticDTO: AnalyticDTO): String = dbQuery {
        Analytic.insert {
            it[secId] = analyticDTO.secId
            it[boardId] = analyticDTO.boardId
            it[open] = analyticDTO.open
            it[low] = analyticDTO.low
            it[high] = analyticDTO.high
            it[last] = analyticDTO.last
            it[updateTime] = analyticDTO.updateTime
            it[time] = analyticDTO.time
        }[Analytic.secId]
    }

    suspend fun getListOfPrices(secId: String, beginPeriod: Int, endPeriod: Int): List<Double> {
        return dbQuery {
            Analytic.select { (Analytic.secId eq secId) and
                (Analytic.time greaterEq beginPeriod) and
                (Analytic.time lessEq endPeriod) }
                .map {
                    it[Analytic.open]
                }
        }
    }

    suspend fun getListOfTime(secId: String, beginPeriod: Int, endPeriod: Int): List<com.google.protobuf.Timestamp> {
        return dbQuery {
            Analytic.select { (Analytic.secId eq secId) and
                (Analytic.time greaterEq beginPeriod) and
                (Analytic.time lessEq endPeriod) }
                .map {
                    AnalyticHelper.integerToTimestamp(it[Analytic.updateTime])
                }
        }
    }
}




