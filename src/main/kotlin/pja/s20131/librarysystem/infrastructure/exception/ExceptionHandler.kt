package pja.s20131.librarysystem.infrastructure.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import pja.s20131.librarysystem.adapter.database.resource.AuthorNotFoundException
import pja.s20131.librarysystem.adapter.database.resource.BookNotFoundException
import pja.s20131.librarysystem.adapter.database.resource.EbookNotFoundException
import pja.s20131.librarysystem.adapter.database.user.UserNotFoundException
import pja.s20131.librarysystem.domain.resource.model.NegativeSizeException
import pja.s20131.librarysystem.domain.user.BadCredentialsException
import pja.s20131.librarysystem.domain.user.EmailAlreadyExistsException
import pja.s20131.librarysystem.domain.user.model.PasswordTooShortException
import pja.s20131.librarysystem.exception.BaseException
import pja.s20131.librarysystem.exception.ErrorCode

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun exceptionMapper(e: Exception): ResponseEntity<ErrorResponse> {
        val errorMessage = when (e) {
            is NegativeSizeException -> e.map(HttpStatus.BAD_REQUEST, ErrorCode.EBOOK_NEGATIVE_FILE_SIZE)
            is PasswordTooShortException -> e.map(HttpStatus.BAD_REQUEST, ErrorCode.PASSWORD_TOO_SHORT)

            is BadCredentialsException -> e.map(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS)

            is AuthorNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.AUTHOR_NOT_FOUND)
            is BookNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.BOOK_NOT_FOUND)
            is EbookNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.EBOOK_NOT_FOUND)
            is UserNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND)

            is EmailAlreadyExistsException -> e.map(HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_EXISTS)

            else -> ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.UNKNOWN_EXCEPTION,
                "Unknown exception occurred"
            )
        }

        return ResponseEntity<ErrorResponse>(errorMessage.toResponse(), errorMessage.status)
    }

    private fun BaseException.map(status: HttpStatus, code: ErrorCode) = ErrorMessage(status, code, message)
}

data class ErrorResponse(
    val status: Int,
    val code: String,
    val timestamp: Long,
    val message: String,
)
