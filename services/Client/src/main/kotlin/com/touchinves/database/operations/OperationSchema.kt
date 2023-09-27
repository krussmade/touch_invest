package com.touchinves.database.operations

import com.touchinves.database.portfolio.PortfolioDTO
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class OperationService(
    private val database: Database
) {
    object Operations : Table() {
        val id = integer("id").autoIncrement()
        val portfolioId = integer("portfolio_id")
        val type = varchar("type", length = 50)
        val secId = integer("sec_id")
        val price = integer("price")
        val qty = integer("qty")
        val date = varchar("date", length = 50)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Operations)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(operation: OperationDTO): Int = dbQuery {
        Operations.insert {
            it[portfolioId] = operation.portfolioId
            it[type] = operation.type
            it[secId] = operation.secId
            it[price] = operation.price
            it[qty] = operation.qty
            it[date] = operation.date
        }[Operations.id]
    }
}