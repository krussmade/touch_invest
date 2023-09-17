package queue

import kotlinx.coroutines.*

class QueueBalancer(private val timeout: Long, private val batches: Int) {

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

    private suspend fun sendToNextQueue(security: String, rec: Int = 0): Boolean {
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
            delay((timeout / batches) * 1000)
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
        val securities = utils.securities
        val jobs = ArrayDeque<Job>(securities.size)

        var epoch = 0

        while (true) {
            try {
                withTimeout(2_000L) {
                    for (i in securities.indices) {
                        if (i % batches != epoch) continue
                        val security = securities[i]
                        jobs.addLast(
                            launch {
                                val success = sendToNextQueue(security)
                                if (!success) {
                                    println("job $security did not perform well")
                                    this@launch.cancel("did not perform well")
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
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                println("Performed " + jobs.count { it.isCompleted }.toString() + " out of " + jobs.size)
                jobs.clear()
                relaxQueues()   // blocking call for (timeout / batches) secs
                epoch = (epoch + 1) % batches
            }
        }
    }
}