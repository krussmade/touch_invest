package com.touchinves.database.users

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*

class UserService(private val database: Database) {
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val login = varchar("login", length = 50)
        val password =  varchar("password", length = 50)
        val telegram =  varchar("telegram", length = 50)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(user: UserDTO): Int = dbQuery {
        Users.insert {
            it[login] = user.login
            it[password] = user.password
            it[telegram] = user.telegram
        }[Users.id]
    }

    suspend fun read(login: String): UserDTO? {
        return dbQuery {
            Users.select { Users.login eq login }
                .map {
                    UserDTO(
                        it[Users.id],
                        it[Users.login],
                        it[Users.password],
                        it[Users.telegram]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun update(login: String, user: UserDTO) {
        dbQuery {
            Users.update({ Users.login eq login }) {
                it[password] = user.password
                it[telegram] = user.telegram
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}
