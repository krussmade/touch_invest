package queue

import kotlinx.coroutines.*
import utils.Security

class QueueBalancer(private val timeout: Long) {

    private val qList = listOf(
        Distributor(),
        Distributor(),
        Distributor(),
    )

    private val queuesCount = qList.size
    private var balancer = 0

    private fun queueReady(): Distributor? {
        val q = qList[balancer]
        return if (q.isOK()) q else null
    }

    private fun tryTurnReserved(): Boolean {
        for (q in qList) {
            if (q.isReserved()) {
                q.prepare()
                return true
            }
        }
        return false
    }

    private suspend fun tryTurnTimeout(): Boolean {
        for (q in qList) {
            if (q.isTimeout()) {
                q.prepareAfterTimeout()
                return true
            }
        }
        return false
    }

    private suspend fun sendToNextQueue(security: Security, rec: Int = 0): Boolean {
        if (rec == queuesCount) {
            if (!tryTurnReserved()) {
                if (!tryTurnTimeout()) {
                    println("No free queues")
                    return false
                }
            }
        }
        if (rec > 2 * queuesCount) {
            println("Unable to delegate to any queue")
            return false
        }

        val q = queueReady()
        balancer = (balancer + 1) % queuesCount

        return q?.distribute(security) ?: sendToNextQueue(security, rec + 1)
    }

    private suspend fun relaxQueues() = runBlocking {
        val relaxAfterRequest = launch {
            delay(timeout * 1000)
        }
        val jobs = ArrayDeque<Job>(queuesCount)
        for (q in qList) {
            jobs.addLast(
                launch {
                    if (q.isTimeout()) q.prepareAfterTimeout() else q.prepare()
                }
            )
        }

        for (job in jobs) {
            job.join()
        }
        qList[balancer].reserve()
        relaxAfterRequest.join()
    }

    fun startPolling() = runBlocking {
        val securities = Security.values()
        val jobs = ArrayDeque<Job>(securities.size)

        while (true) {
            try {
                withTimeout(2_000L) {
                    for (security in securities) {
                        jobs.addLast(
                            launch {
                                val success = sendToNextQueue(security)
                                if (!success) {
                                    println("job $security did not perform well")
                                    cancel()
                                }
                            }
                        )
                    }
                    for (job in jobs) {
                        job.join()
                    }
                }
            } catch (e: TimeoutCancellationException) {
                for (job in jobs) {
                    if (job.isCancelled || job.isCompleted) {
                        continue
                    }
                    job.cancelAndJoin()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                println("Performed " + jobs.count { it.isCompleted }.toString() + " out of " + securities.size)
                jobs.clear()
                relaxQueues()
            }
        }
    }
}