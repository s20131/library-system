package pja.s20131.librarysystem.adapter.exceptions

sealed class RestException(message: String) : RuntimeException(message)

open class BadRequestException(message: String) : RestException(message)

open class ForbiddenException(message: String) : RestException(message)

open class NotFoundException(message: String) : RestException(message)
