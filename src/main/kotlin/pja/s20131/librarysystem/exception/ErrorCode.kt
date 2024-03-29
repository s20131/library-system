package pja.s20131.librarysystem.exception

enum class ErrorCode {
    AUTHOR_NOT_FOUND,
    BAD_CREDENTIALS,
    BOOK_NOT_FOUND,
    CANNOT_RESERVE_RESOURCE,
    COPY_NOT_FOUND,
    COVER_NOT_FOUND,
    EBOOK_NOT_FOUND,
    EBOOK_NEGATIVE_FILE_SIZE,
    EMAIL_ALREADY_EXISTS,
    INVALID_POSTCODE_PATTERN,
    LIBRARIAN_NOT_WORKING_AT_LIBRARY,
    LIBRARY_CARD_NOT_FOUND,
    LIBRARY_NOT_SELECTED,
    NOT_ENOUGH_COPIES,
    PASSWORD_TOO_SHORT,
    RENTAL_ALREADY_ACTIVE,
    RENTAL_CANNOT_BE_DOWNLOADED,
    RENTAL_NOT_ACTIVE,
    RENTAL_NOT_FOUND,
    RENTAL_PERIOD_NOT_OVERLAPPED,
    RESERVATION_NOT_FOUND,
    RESOURCE_NOT_FOUND,
    UNKNOWN_EXCEPTION,
    UNSUPPORTED_EBOOK_FORMAT,
    USER_NOT_FOUND,
}
