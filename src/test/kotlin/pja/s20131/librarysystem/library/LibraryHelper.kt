package pja.s20131.librarysystem.library

import com.github.javafaker.Faker
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.domain.library.Address
import pja.s20131.librarysystem.domain.library.City
import pja.s20131.librarysystem.domain.library.Library
import pja.s20131.librarysystem.domain.library.LibraryId
import pja.s20131.librarysystem.domain.library.LibraryName
import pja.s20131.librarysystem.domain.library.Postcode
import pja.s20131.librarysystem.domain.library.StreetName
import pja.s20131.librarysystem.domain.library.StreetNumber

val faker = Faker()

fun library(
    id: LibraryId = LibraryId.generate(),
    name: LibraryName = LibraryName(faker.witcher().school()),
    streetName: StreetName = StreetName(faker.address().streetName()),
    streetNumber: StreetNumber = StreetNumber(faker.address().streetAddressNumber()),
    postcode: Postcode = Postcode("00-111"),
    city: City = City(faker.address().city()),
) = Library(id, name, Address(streetName, streetNumber, postcode, city))

fun addLibrary(library: Library) {
    transaction {
        LibraryTable.insert {
            it[id] = library.libraryId.raw
            it[name] = library.libraryName.raw
            it[streetName] = library.address.streetName.raw
            it[streetNumber] = library.address.streetNumber.raw
            it[postcode] = library.address.postcode.raw
            it[city] = library.address.city.raw
        }
    }
}
