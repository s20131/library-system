package pja.s20131.librarysystem.infrastructure.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import pja.s20131.librarysystem.adapter.database.library.UserHasNotSelectedLibraryException
import pja.s20131.librarysystem.adapter.database.resource.BookNotFoundException
import pja.s20131.librarysystem.adapter.database.resource.CopyNotFoundException
import pja.s20131.librarysystem.adapter.database.resource.CoverNotFoundException
import pja.s20131.librarysystem.adapter.database.resource.EbookNotFoundException
import pja.s20131.librarysystem.adapter.database.resource.ReservationNotFoundException
import pja.s20131.librarysystem.adapter.database.resource.ResourceNotFoundException
import pja.s20131.librarysystem.adapter.database.user.LibraryCardDoesNotExistException
import pja.s20131.librarysystem.domain.library.model.InvalidPostcodePatternException
import pja.s20131.librarysystem.domain.resource.CannotReserveResourceException
import pja.s20131.librarysystem.domain.resource.InsufficientCopyAvailabilityException
import pja.s20131.librarysystem.domain.resource.UserNotPermittedToAccessLibraryException
import pja.s20131.librarysystem.domain.resource.model.NegativeSizeException
import pja.s20131.librarysystem.domain.resource.model.RentalAlreadyActiveException
import pja.s20131.librarysystem.domain.resource.model.RentalCannotBeDownloadedException
import pja.s20131.librarysystem.domain.resource.model.RentalNotActiveException
import pja.s20131.librarysystem.domain.resource.model.RentalPeriodNotOverlappingDatesException
import pja.s20131.librarysystem.domain.resource.port.AuthorNotFoundException
import pja.s20131.librarysystem.domain.resource.port.RentalNotFoundException
import pja.s20131.librarysystem.domain.user.BadCredentialsException
import pja.s20131.librarysystem.domain.user.EmailAlreadyExistsException
import pja.s20131.librarysystem.domain.user.model.PasswordTooShortException
import pja.s20131.librarysystem.domain.user.port.UserNotFoundException
import pja.s20131.librarysystem.exception.BaseException
import pja.s20131.librarysystem.exception.ErrorCode

@ControllerAdvice
class ExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    fun exceptionMapper(e: Exception): ResponseEntity<ErrorResponse> {
        val errorMessage = when (e) {
            // 400
            is CannotReserveResourceException -> e.map(HttpStatus.BAD_REQUEST, ErrorCode.CANNOT_RESERVE_RESOURCE)
            is InvalidPostcodePatternException -> e.map(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_POSTCODE_PATTERN)
            is NegativeSizeException -> e.map(HttpStatus.BAD_REQUEST, ErrorCode.EBOOK_NEGATIVE_FILE_SIZE)
            is PasswordTooShortException -> e.map(HttpStatus.BAD_REQUEST, ErrorCode.PASSWORD_TOO_SHORT)
            is RentalAlreadyActiveException -> e.map(HttpStatus.BAD_REQUEST, ErrorCode.RENTAL_ALREADY_ACTIVE)
            is RentalCannotBeDownloadedException -> e.map(HttpStatus.BAD_REQUEST, ErrorCode.RENTAL_CANNOT_BE_DOWNLOADED)
            is RentalNotActiveException -> e.map(HttpStatus.BAD_REQUEST, ErrorCode.RENTAL_NOT_ACTIVE)
            is RentalPeriodNotOverlappingDatesException -> e.map(HttpStatus.BAD_REQUEST, ErrorCode.RENTAL_PERIOD_NOT_OVERLAPPED)
            is UserHasNotSelectedLibraryException -> e.map(HttpStatus.BAD_REQUEST, ErrorCode.LIBRARY_NOT_SELECTED)

            // 401
            is BadCredentialsException -> e.map(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS)

            // 403
            is UserNotPermittedToAccessLibraryException -> e.map(HttpStatus.FORBIDDEN, ErrorCode.LIBRARIAN_NOT_WORKING_AT_LIBRARY)

            // 404
            is AuthorNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.AUTHOR_NOT_FOUND)
            is BookNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.BOOK_NOT_FOUND)
            is CopyNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.COPY_NOT_FOUND)
            is CoverNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.COVER_NOT_FOUND)
            is EbookNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.EBOOK_NOT_FOUND)
            is LibraryCardDoesNotExistException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.LIBRARY_CARD_NOT_FOUND)
            is RentalNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.RENTAL_NOT_FOUND)
            is ReservationNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.RESERVATION_NOT_FOUND)
            is ResourceNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND)
            is UserNotFoundException -> e.map(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND)

            // 409
            is EmailAlreadyExistsException -> e.map(HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_EXISTS)
            is InsufficientCopyAvailabilityException -> e.map(HttpStatus.CONFLICT, ErrorCode.NOT_ENOUGH_COPIES)

            // 500
            else -> ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN_EXCEPTION, "Unknown exception occurred")
        }
        when (errorMessage.status) {
            HttpStatus.INTERNAL_SERVER_ERROR -> logger.error(errorMessage.toString())
            else -> logger.warn(errorMessage.toString())
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
