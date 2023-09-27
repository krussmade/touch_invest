package requester

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withTimeout
import proto.AnalyticsGrpcKt
import proto.Security
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class AnalyticsClient(private val useGRPC: Boolean = true) {
    private val host = "analytics"
    private val port = 50051
    private val portGRPC = 50052

    private val client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build()

    private val channel = ManagedChannelBuilder.forAddress(host, portGRPC).usePlaintext().build()
    private val stub = AnalyticsGrpcKt.AnalyticsCoroutineStub(channel)

    suspend fun sendSecurity(security: Security.TSecurity, timeout: Long): Boolean {
        return try {
            withTimeout(timeout) {
                if (useGRPC) sendSecurityGRPC(security) else sendSecurityHTTP(security)
            }
        } catch (e: TimeoutCancellationException) {
            println("Analytics timeout")
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun sendSecurityHTTP(security: Security.TSecurity): Boolean {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://$host:$port/security/send"))
            .POST(HttpRequest.BodyPublishers.ofByteArray(security.toByteArray()))
            .build()

        val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await()
        return if (response.statusCode() in 200..299) {
            true
        } else {
            println("Analytics fail: ${response.body()}")
            false
        }
    }

    private suspend fun sendSecurityGRPC(security: Security.TSecurity): Boolean {
        val response = stub.sendSecurity(security)
        return if (response.result == Security.TAnalyticsSendSecurityResponse.SecurityInsertResult.OK) {
            true
        } else {
            println(response.reason)
            false
        }
    }
}