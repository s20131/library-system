package pja.s20131.librarysystem.library

import net.datafaker.Faker
import net.postgis.jdbc.geometry.Point
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.library.model.Address
import pja.s20131.librarysystem.domain.library.model.City
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.library.model.Location
import pja.s20131.librarysystem.domain.library.model.Postcode
import pja.s20131.librarysystem.domain.library.model.StreetName
import pja.s20131.librarysystem.domain.library.model.StreetNumber
import pja.s20131.librarysystem.domain.resource.model.Availability
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId

@Component
class LibraryPreconditions(
    private val libraryDatabaseHelper: LibraryDatabaseHelper,
    private val librarianDatabaseHelper: LibrarianDatabaseHelper,
    private val copyDatabaseHelper: CopyDatabaseHelper,
) {
    private val faker = Faker()

    fun exists(
        name: LibraryName = LibraryName(faker.witcher().school()),
        streetName: StreetName = StreetName(faker.address().streetName()),
        streetNumber: StreetNumber = StreetNumber(faker.address().streetAddressNumber()),
        postcode: Postcode = Postcode("00-111"),
        city: City = City(faker.address().city()),
        location: Location = Location(Point(faker.address().longitude().toDouble(), faker.address().latitude().toDouble()))
    ): Builder {
        val library = Library(LibraryId.generate(), name, Address(streetName, streetNumber, postcode, city, location))
        libraryDatabaseHelper.insertLibrary(library)
        return Builder(library)
    }

    inner class Builder(private val library: Library) {

        fun hasCopy(resourceId: ResourceId, availability: Availability = Availability(2)): Builder {
            copyDatabaseHelper.insertCopy(library.libraryId, resourceId, availability)
            return this
        }

        fun hasLibrarian(userId: UserId, isSelected: Boolean = true): Builder {
            librarianDatabaseHelper.insertLibrarian(userId, library.libraryId, isSelected)
            return this
        }

        fun build(): Library {
            return library
        }
    }
}
