package pja.s20131.librarysystem.infrastructure.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun badRequestExceptionMapper(exception: BadRequestException, request: WebRequest) =
        BasicException(
            status = HttpStatus.BAD_REQUEST,
            message = exception.message!!,
            description = request.getDescription(false)
        )

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun forbiddenExceptionMapper(exception: ForbiddenException, request: WebRequest) =
        BasicException(
            status = HttpStatus.FORBIDDEN,
            message = exception.message!!,
            description = request.getDescription(false)
        )

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFoundExceptionMapper(exception: NotFoundException, request: WebRequest) =
        BasicException(
            status = HttpStatus.NOT_FOUND,
            message = exception.message!!,
            description = request.getDescription(false)
        )

    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun generalExceptionMapper(exception: RuntimeException, request: WebRequest) =
        BasicException(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = exception.message!!,
            description = request.getDescription(false),
        )
}