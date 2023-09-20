import queue.QueueBalancer

fun main(args: Array<String>) {
    val timeout = if (args.any { it.startsWith("timeout") }) {
        val parsedTimeout = args.first { it.startsWith("timeout") }
        parsedTimeout.split("=")[1].toLong()
    } else { 5*60L }

    val batches = if (args.any { it.startsWith("batches") }) {
        val parsedTimeout = args.first { it.startsWith("batches") }
        parsedTimeout.split("=")[1].toInt()
    } else { 20 }

    val queueBalancer = QueueBalancer(timeout, batches)
    queueBalancer.startPolling()

    TODO("check trading dates - https://iss.moex.com/iss/engines/stock/timetable")
}