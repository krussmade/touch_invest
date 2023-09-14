import queue.QueueBalancer

fun main(args: Array<String>) {
    val timeout = if (args.any { it.startsWith("timeout") }) {
        val parsedTimeout = args.first { it.startsWith("timeout") }
        parsedTimeout.split("=")[1].toLong()
    } else { 20L }

    val queueBalancer = QueueBalancer(timeout)
    queueBalancer.startPolling()

    TODO(
        "add all securities" +
                "add proto" +
                "add response parser and proto converter" +
                "check trading dates"
    )
}