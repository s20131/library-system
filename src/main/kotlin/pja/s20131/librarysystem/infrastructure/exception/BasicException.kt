package pja.s20131.librarysystem.infrastructure.exception

import java.time.LocalDateTime
import org.springframework.http.HttpStatus

class BasicException(
    val status: HttpStatus,
    val time: LocalDateTime = LocalDateTime.now(),
    val message: String,
    val description: String,
)
