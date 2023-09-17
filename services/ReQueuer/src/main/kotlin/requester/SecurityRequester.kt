package requester

import utils.DataParseException
import utils.NullDataException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class SecurityRequester(private val securityName: String, private val client: HttpClient) {
    private fun JsonArray.getTQBR(): JsonArray {
        for (i in this) {
            if (i.jsonArray[1].jsonPrimitive.content == "TQBR") {
                return i.jsonArray
            }
        }
        throw DataParseException("No TQBR market data")
    }
    private fun getSecurityDataRow(unparsedData: String): JsonArray {
        val json = Json.parseToJsonElement(unparsedData)
        val map = json.jsonObject.toMap()

        val tradingData = map["marketdata"]?.jsonObject?.get("data")?.jsonArray!!
        val data = tradingData.getTQBR()

        try {
            return data
        } catch (e: AssertionError) {
            e.printStackTrace()
            throw NullDataException()
        } catch (e: Throwable) {
            e.printStackTrace()
            throw DataParseException()
        }
    }

    private suspend fun performRequest() = coroutineScope<String> {
        val url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities/$securityName.json"

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()

        val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        return@coroutineScope response.await().body() // suspend and return String not a Future
    }

    suspend fun getSecurityData(): JsonArray {
        return getSecurityDataRow(
            performRequest()
        )
    }
}
