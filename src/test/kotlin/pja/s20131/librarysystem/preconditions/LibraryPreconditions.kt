package pja.s20131.librarysystem.preconditions

import net.datafaker.Faker
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.library.model.Address
import pja.s20131.librarysystem.domain.library.model.City
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.library.model.Postcode
import pja.s20131.librarysystem.domain.library.model.StreetName
import pja.s20131.librarysystem.domain.library.model.StreetNumber
import pja.s20131.librarysystem.library.LibraryDatabaseHelper

@Component
class LibraryPreconditions(
    private val libraryDatabaseHelper: LibraryDatabaseHelper,
) {
    private val faker = Faker()

    fun exists(
        name: LibraryName = LibraryName(faker.witcher().school()),
        streetName: StreetName = StreetName(faker.address().streetName()),
        streetNumber: StreetNumber = StreetNumber(faker.address().streetAddressNumber()),
        postcode: Postcode = Postcode("00-111"),
        city: City = City(faker.address().city()),
    ): Library {
        val library = Library(LibraryId.generate(), name, Address(streetName, streetNumber, postcode, city))
        libraryDatabaseHelper.insertLibrary(library)
        return library
    }
}
