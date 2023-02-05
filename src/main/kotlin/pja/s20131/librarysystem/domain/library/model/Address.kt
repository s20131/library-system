package pja.s20131.librarysystem.domain.library.model

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
value class Postcode(val value: String)

@JvmInline
value class City(val value: String)
