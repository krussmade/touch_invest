package utils

open class QueueBaseException(override val message: String) : RuntimeException(message)

class DataParseException(override val message: String = "unable to parse response") : QueueBaseException(message)

class NullDataException(override val message: String = "invalid response from MOEX"): QueueBaseException(message)