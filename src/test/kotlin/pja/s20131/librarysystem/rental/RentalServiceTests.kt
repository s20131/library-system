package pja.s20131.librarysystem.rental

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.adapter.database.resource.RentalNotFoundException
import pja.s20131.librarysystem.domain.resource.CannotBorrowResourceException
import pja.s20131.librarysystem.domain.resource.RentalHistory
import pja.s20131.librarysystem.domain.resource.RentalService
import pja.s20131.librarysystem.domain.resource.RentalShortInfo
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import pja.s20131.librarysystem.domain.resource.model.ResourceType

@SpringBootTest
class RentalServiceTests @Autowired constructor(
    private val rentalService: RentalService,
    private val given: Preconditions,
    private val assert: pja.s20131.librarysystem.Assertions,
) : BaseTestConfig() {

    @Test
    fun `should return user rentals`() {
        val user = given.user.exists()
        val (author, books) = given.author.exists().withBook().build()
        val library = given.library.exists().hasCopy(books[0].resourceId).build()
        val rental = given.rental.exists(user.userId, books[0].resourceId, library.libraryId)

        val response = rentalService.getUserRentals(user.userId)

        assertThat(response).containsExactly(
            RentalHistory(
                library.libraryId,
                books[0].toBasicData(),
                author.toBasicData(),
                rental.rentalPeriod.startDate,
                RentalStatus.ACTIVE,
                ResourceType.BOOK
            )
        )
    }

    @Test
    fun `should return user rentals sorted by finishing earliest`() {
        val user = given.user.exists()
        val (author, books, ebooks) = given.author.exists().withBook().withEbook().build()
        val library = given.library.exists().hasCopy(books[0].resourceId).hasCopy(ebooks[0].resourceId).build()
        val rental1 = given.rental.exists(user.userId, books[0].resourceId, library.libraryId)
        val rental2 = given.rental.exists(user.userId, ebooks[0].resourceId, library.libraryId, clock.yesterday())

        val response = rentalService.getUserRentals(user.userId)

        assertThat(response).containsExactly(
            RentalHistory(
                library.libraryId,
                ebooks[0].toBasicData(),
                author.toBasicData(),
                rental2.rentalPeriod.startDate,
                RentalStatus.ACTIVE,
                ResourceType.EBOOK,
            ),
            RentalHistory(
                library.libraryId,
                books[0].toBasicData(),
                author.toBasicData(),
                rental1.rentalPeriod.startDate,
                RentalStatus.ACTIVE,
                ResourceType.BOOK,
            ),
        )
    }

    @Test
    fun `should get info about latest user rental of the same resource`() {
        val user = given.user.exists()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        given.rental.exists(user.userId, book.resourceId, library.libraryId, clock.lastWeek())
        val expectedRental = given.rental.exists(user.userId, book.resourceId, library.libraryId)
        given.rental.exists(user.userId, book.resourceId, library.libraryId, clock.yesterday())

        val response = rentalService.getLatestRentalShortInfo(book.resourceId, user.userId)

        assertThat(response).isEqualTo(
            RentalShortInfo(
                expectedRental.rentalStatus,
                expectedRental.rentalPeriod.finishTime,
                library.libraryName,
                penalty = null,
            )
        )
    }

    @Test
    fun `should throw exception when trying to get info about not existing rental`() {
        val user = given.user.exists()
        val book = given.author.exists().withBook().build().second[0]

        assertThrows<RentalNotFoundException> { rentalService.getLatestRentalShortInfo(book.resourceId, user.userId) }
    }

    @Test
    fun `should borrow a resource`() {
        val user = given.user.exists()
        val ebook = given.author.exists().withEbook().build().third[0]
        val library = given.library.exists().hasCopy(ebook.resourceId).build()

        rentalService.borrowResource(ebook.resourceId, library.libraryId, user.userId)

        assert.rental.isSaved(
            user.userId,
            ebook.resourceId,
            library.libraryId,
            RentalPeriod(clock.now(), clock.inDays(14)),
            RentalStatus.ACTIVE,
            penalty = null
        )
    }

    @Test
    fun `should reserve a book for user to pickup after borrowing one`() {
        val user = given.user.exists()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()

        rentalService.borrowResource(book.resourceId, library.libraryId, user.userId)

        assert.rental.isSaved(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod(clock.now(), clock.inDays(2)),
            RentalStatus.RESERVED_TO_BORROW,
            penalty = null
        )
    }

    @Test
    fun `should throw exception when trying to borrow a resource with 0 copies in a library`() {
        val user = given.user.exists()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId, Available(0)).build()

        assertThrows<CannotBorrowResourceException> {
            rentalService.borrowResource(book.resourceId, library.libraryId, user.userId)
        }
    }
}
