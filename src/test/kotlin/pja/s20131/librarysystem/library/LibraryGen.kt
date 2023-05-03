package pja.s20131.librarysystem.library

import net.datafaker.Faker
import pja.s20131.librarysystem.domain.library.model.Address
import pja.s20131.librarysystem.domain.library.model.City
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.library.model.Postcode
import pja.s20131.librarysystem.domain.library.model.StreetName
import pja.s20131.librarysystem.domain.library.model.StreetNumber

object LibraryGen {
    private val faker = Faker()

    fun library(
        id: LibraryId = LibraryId.generate(),
        name: LibraryName = LibraryName(faker.witcher().school()),
        streetName: StreetName = StreetName(faker.address().streetName()),
        streetNumber: StreetNumber = StreetNumber(faker.address().streetAddressNumber()),
        postcode: Postcode = Postcode("00-111"),
        city: City = City(faker.address().city()),
    ) = Library(id, name, Address(streetName, streetNumber, postcode, city))
}
