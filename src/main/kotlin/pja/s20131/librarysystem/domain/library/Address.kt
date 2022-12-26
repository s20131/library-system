package pja.s20131.librarysystem.domain.library

data class Address(
    val streetName: StreetName,
    val streetNumber: StreetNumber,
    val postcode: Postcode,
    val city: City
)

@JvmInline
value class StreetName(val raw: String)

@JvmInline
value class StreetNumber(val raw: String)

@JvmInline
value class Postcode(val raw: String)

@JvmInline
value class City(val raw: String)
