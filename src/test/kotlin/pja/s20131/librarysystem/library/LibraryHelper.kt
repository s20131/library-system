package pja.s20131.librarysystem.library

import com.github.javafaker.Faker
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.domain.library.model.Address
import pja.s20131.librarysystem.domain.library.model.City
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.library.model.Postcode
import pja.s20131.librarysystem.domain.library.model.StreetName
import pja.s20131.librarysystem.domain.library.model.StreetNumber

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
            it[id] = library.libraryId.value
            it[name] = library.libraryName.value
            it[streetName] = library.address.streetName.value
            it[streetNumber] = library.address.streetNumber.value
            it[postcode] = library.address.postcode.value
            it[city] = library.address.city.value
        }
    }
}
