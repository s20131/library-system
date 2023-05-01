package pja.s20131.librarysystem.infrastructure.exception

import java.time.Instant
import org.springframework.http.HttpStatus
import pja.s20131.librarysystem.exception.ErrorCode

data class ErrorMessage(
    val status: HttpStatus,
    val code: ErrorCode,
    val timestamp: Long,
    val message: String,
) {
    constructor(status: HttpStatus, code: ErrorCode, message: String) : this(
        status,
        code,
        Instant.now().toEpochMilli(),
        message
    )

    fun toResponse(): ErrorResponse = ErrorResponse(status.value(), code.name, timestamp, message)
}
