package pja.s20131.librarysystem.library

import net.postgis.jdbc.geometry.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.domain.library.LibraryService
import pja.s20131.librarysystem.domain.library.ResourceCopy
import pja.s20131.librarysystem.domain.library.model.Location
import pja.s20131.librarysystem.domain.resource.UserNotPermittedToAccessLibraryException
import pja.s20131.librarysystem.domain.resource.model.Availability

@SpringBootTest
class LibraryServiceTests @Autowired constructor(
    val libraryService: LibraryService,
    val given: Preconditions,
) : BaseTestConfig() {

    @Test
    fun `should get all libraries`() {
        val library1 = given.library.exists().build()
        val library2 = given.library.exists().build()

        val response = libraryService.getAllLibraries()

        assertThat(response).containsOnly(library1, library2)
    }

    @Test
    fun `should get copies in libraries`() {
        val (_, books) = given.author.exists().withBook().build()
        val library = given.library.exists().hasCopy(books[0].resourceId).build()

        val response = libraryService.getResourceCopiesInLibraries(books[0].resourceId, userLocation = null)

        assertThat(response).containsExactly(ResourceCopy(library, Availability(2), distance = null))
    }

    @Test
    fun `should get copies in libraries ordered by availability if user location is not provided`() {
        val (_, books) = given.author.exists().withBook().build()
        val library1 = given.library.exists().hasCopy(books[0].resourceId).build()
        val library2 = given.library.exists().hasCopy(books[0].resourceId, Availability(3)).build()

        val response = libraryService.getResourceCopiesInLibraries(books[0].resourceId, userLocation = null)

        assertThat(response).containsExactly(
            ResourceCopy(library2, Availability(3), distance = null),
            ResourceCopy(library1, Availability(2), distance = null),
        )
    }

    @Test
    fun `should get copies in libraries ordered by user location`() {
        val userLocation = Point(0.0, 0.0)
        val (_, books) = given.author.exists().withBook().build()
        val library1 = given.library.exists(location = Location(Point(1.0, 1.0))).hasCopy(books[0].resourceId, Availability(1)).build()
        val library2 = given.library.exists(location = Location(Point(10.0, 10.0))).hasCopy(books[0].resourceId, Availability(3)).build()

        val response = libraryService.getResourceCopiesInLibraries(books[0].resourceId, userLocation)

        assertThat(response).containsExactly(
            // not a good practice, but exact distance doesn't matter
            ResourceCopy(library1, Availability(1), distance = response[0].distance!!),
            ResourceCopy(library2, Availability(3), distance = response[1].distance!!),
        )
    }

    @Test
    fun `should put copies with 0 availability at the end`() {
        val userLocation = Point(0.0, 0.0)
        val (_, books) = given.author.exists().withBook().build()
        val library1 = given.library.exists(location = Location(Point(1.0, 1.0))).hasCopy(books[0].resourceId, Availability(0)).build()
        val library2 = given.library.exists(location = Location(Point(10.0, 10.0))).hasCopy(books[0].resourceId, Availability(1)).build()
        val library3 = given.library.exists(location = Location(Point(100.0, 100.0))).hasCopy(books[0].resourceId, Availability(3)).build()

        val response = libraryService.getResourceCopiesInLibraries(books[0].resourceId, userLocation)

        assertThat(response).containsExactly(
            // not a good practice, but exact distance doesn't matter
            ResourceCopy(library2, Availability(1), distance = response[0].distance!!),
            ResourceCopy(library3, Availability(3), distance = response[1].distance!!),
            ResourceCopy(library1, Availability(0), distance = response[2].distance!!),
        )
    }

    @Test
    fun `should get availability of a given resource`() {
        val (_, books) = given.author.exists().withBook().build()
        val library = given.library.exists().hasCopy(books[0].resourceId).build()

        val response = libraryService.getAvailability(library.libraryId, books[0].resourceId)

        assertThat(response).isEqualTo(Availability(2))
    }

    @Test
    fun `should update copies availability of a given resource`() {
        val (_, books) = given.author.exists().withBook().build()
        val librarian = given.user.exists().build()
        val library = given.library.exists().hasCopy(books[0].resourceId).hasLibrarian(librarian.userId).build()

        libraryService.changeAvailability(library.libraryId, books[0].resourceId, Availability(3), librarian.userId)

        val response = libraryService.getAvailability(library.libraryId, books[0].resourceId)
        assertThat(response).isEqualTo(Availability(3))
    }

    @Test
    fun `should insert copies availability of a given resource`() {
        val (_, _, ebooks) = given.author.exists().withEbook().build()
        val librarian = given.user.exists().build()
        val library = given.library.exists().hasLibrarian(librarian.userId).build()

        libraryService.changeAvailability(library.libraryId, ebooks[0].resourceId, Availability(2), librarian.userId)

        val response = libraryService.getAvailability(library.libraryId, ebooks[0].resourceId)
        assertThat(response).isEqualTo(Availability(2))
    }

    @Test
    fun `should throw an exception when unauthorized user tries to update availability`() {
        val (_, books) = given.author.exists().withBook().build()
        val librarian = given.user.exists().build()
        val library = given.library.exists().hasCopy(books[0].resourceId).build()

        assertThrows<UserNotPermittedToAccessLibraryException> {
            libraryService.changeAvailability(library.libraryId, books[0].resourceId, Availability(3), librarian.userId)
        }
    }
}
