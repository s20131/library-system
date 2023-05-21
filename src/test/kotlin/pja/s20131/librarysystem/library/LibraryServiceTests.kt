package pja.s20131.librarysystem.library

import net.postgis.jdbc.geometry.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.domain.library.LibraryService
import pja.s20131.librarysystem.domain.library.ResourceCopy
import pja.s20131.librarysystem.domain.library.model.Location
import pja.s20131.librarysystem.domain.resource.model.Available

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
        val library1 = given.library.exists().hasCopy(books[0].resourceId).build()

        val response = libraryService.getResourceCopiesInLibraries(books[0].resourceId, userLocation = null)

        assertThat(response).containsExactly(ResourceCopy(library1, Available(2), distance = null))
    }

    @Test
    fun `should get copies in libraries ordered by availability if user location is not provided`() {
        val (_, books) = given.author.exists().withBook().build()
        val library1 = given.library.exists().hasCopy(books[0].resourceId).build()
        val library2 = given.library.exists().hasCopy(books[0].resourceId, Available(3)).build()

        val response = libraryService.getResourceCopiesInLibraries(books[0].resourceId, userLocation = null)

        assertThat(response).containsExactly(
            ResourceCopy(library2, Available(3), distance = null),
            ResourceCopy(library1, Available(2), distance = null),
        )
    }

    @Test
    fun `should get copies in libraries ordered by user location`() {
        val userLocation = Point(0.0, 0.0)
        val (_, books) = given.author.exists().withBook().build()
        val library1 = given.library.exists(location = Location(Point(1.0, 1.0))).hasCopy(books[0].resourceId, Available(1)).build()
        val library2 = given.library.exists(location = Location(Point(10.0, 10.0))).hasCopy(books[0].resourceId, Available(3)).build()

        val response = libraryService.getResourceCopiesInLibraries(books[0].resourceId, userLocation)

        assertThat(response).containsExactly(
            // not a good practice, but exact distance doesn't matter
            ResourceCopy(library1, Available(1), distance = response[0].distance!!),
            ResourceCopy(library2, Available(3), distance = response[1].distance!!),
        )
    }

    @Test
    fun `should put copies with 0 availability at the end`() {
        val userLocation = Point(0.0, 0.0)
        val (_, books) = given.author.exists().withBook().build()
        val library1 = given.library.exists(location = Location(Point(1.0, 1.0))).hasCopy(books[0].resourceId, Available(0)).build()
        val library2 = given.library.exists(location = Location(Point(10.0, 10.0))).hasCopy(books[0].resourceId, Available(1)).build()
        val library3 = given.library.exists(location = Location(Point(100.0, 100.0))).hasCopy(books[0].resourceId, Available(3)).build()

        val response = libraryService.getResourceCopiesInLibraries(books[0].resourceId, userLocation)

        assertThat(response).containsExactly(
            // not a good practice, but exact distance doesn't matter
            ResourceCopy(library2, Available(1), distance = response[0].distance!!),
            ResourceCopy(library3, Available(3), distance = response[1].distance!!),
            ResourceCopy(library1, Available(0), distance = response[2].distance!!),
        )
    }
}
