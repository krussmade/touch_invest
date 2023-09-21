package com.touchinves.database.portfolio

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*

class PortfolioService(private val database: Database) {
    object Portfolios : Table() {
        val id = integer("id").autoIncrement()
        val ownerId = integer("ownerId")
        val name =  varchar("name", length = 50)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Portfolios)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(portfolio: PortfolioDTO): Int = dbQuery {
        Portfolios.insert {
            it[ownerId] = portfolio.ownerId
            it[name] = portfolio.name
        }[Portfolios.id]
    }

    suspend fun read(id: Int): PortfolioDTO? {
        return dbQuery {
            Portfolios.select { Portfolios.id eq id }
                .map {
                    PortfolioDTO(
                        it[Portfolios.id],
                        it[Portfolios.ownerId],
                        it[Portfolios.name]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: PortfolioDTO) {
        dbQuery {
            Portfolios.update({ Portfolios.id eq id }) {
                it[ownerId] = user.ownerId
                it[name] = name
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Portfolios.deleteWhere { Portfolios.id.eq(id) }
        }
    }
}