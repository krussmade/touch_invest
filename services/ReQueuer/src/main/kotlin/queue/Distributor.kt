package queue

import utils.QueueBaseException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonArray
import requester.SecurityRequester
import utils.AnalyticsClient
import utils.convertToSecurityProto
import java.net.http.HttpClient

class Distributor {
    private enum class Status {
        OK,
        TIMEOUT,
        RESERVED,
    }

    private var status = Status.OK

    private val analyticsClient = AnalyticsClient()
    private val httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build()

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

    private suspend fun sendToAnalytics(data: JsonArray): Boolean {
        // pack to protobuf
        val security = convertToSecurityProto(data)

        // send
        return analyticsClient.sendSecurity(security, 1_500L)
    }

    suspend fun distribute(security: String): Boolean {
        // call and parse
        val r = SecurityRequester(security, httpClient)
        val dataParsed: JsonArray = try {
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