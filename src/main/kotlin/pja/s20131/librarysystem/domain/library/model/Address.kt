package pja.s20131.librarysystem.domain.library.model

import pja.s20131.librarysystem.exception.BaseException

data class Address(
    val streetName: StreetName,
    val streetNumber: StreetNumber,
    val postcode: Postcode,
    val city: City
)

@JvmInline
value class StreetName(val value: String)

@JvmInline
value class StreetNumber(val value: String)

@JvmInline
value class Postcode(val value: String) {
    init {
        if (validPattern.matches(value).not()) throw InvalidPostcodePatternException(value)
    }

    companion object {
        val validPattern = """\d{2}-\d{3}""".toRegex()
    }
}

@JvmInline
value class City(val value: String)

class InvalidPostcodePatternException(value: String) :
    BaseException("Expected postcode to match \"2 digits, dash, 3 digits\" pattern, but was $value")
