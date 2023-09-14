package queue

import utils.QueueBaseException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import requester.SecurityRequester
import utils.Security
import java.net.http.HttpClient

class Distributor {
    private enum class Status {
        OK,
        TIMEOUT,
        RESERVED,
    }

    private val client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build()
    private var status = Status.OK

    fun isOK() = (status == Status.OK)

    fun isTimeout() = (status == Status.TIMEOUT)

    fun isReserved() = (status == Status.RESERVED)

    fun reserve() {
        if (status == Status.TIMEOUT) {
            throw RuntimeException("Cannot reserve timeout queue")
        }
        status = Status.RESERVED
    }

    fun prepare() {
        if (status == Status.TIMEOUT) {
            throw RuntimeException("Cannot prepare timeout queue")
        }
        status = Status.OK
    }

    suspend fun prepareAfterTimeout() {
        delay(2_000L)
        status = Status.OK
    }

    private suspend fun String.convertToProto(): String {
//        return this // add parse utils when protobuf included
        return this.substring(0, 10)
    }

    private suspend fun sendToAnalytics(data: String): Boolean {
        // pack to protobuf

        // send
        println(data.convertToProto())

        // await response
        return true
    }

    suspend fun distribute(security: Security): Boolean {
        // call and parse
        val r = SecurityRequester(security.name, client)
        val dataParsed: String = try {
            withTimeout(500) {
                r.getSecurityData()
            }
        } catch (e: TimeoutCancellationException) {
            status = Status.TIMEOUT
            return false
        } catch (e: QueueBaseException) {
            println(e.message)
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        // parse data

        // send to Analytics
        return try {
            withTimeout(1_500) {
                sendToAnalytics(dataParsed)
            }
        } catch (e: TimeoutCancellationException) {
            status = Status.TIMEOUT
            false
        }
    }
}