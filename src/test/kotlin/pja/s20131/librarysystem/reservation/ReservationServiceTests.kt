package pja.s20131.librarysystem.reservation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.IntegrationTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.adapter.database.resource.ReservationNotFoundException
import pja.s20131.librarysystem.domain.resource.CannotReserveResourceException
import pja.s20131.librarysystem.domain.resource.ReservationHistory
import pja.s20131.librarysystem.domain.resource.ReservationService
import pja.s20131.librarysystem.domain.resource.ReservationShortInfo
import pja.s20131.librarysystem.domain.resource.model.Availability
import pja.s20131.librarysystem.domain.resource.model.ResourceType

@SpringBootTest
class ReservationServiceTests @Autowired constructor(
    private val reservationService: ReservationService,
    private val given: Preconditions,
    private val assert: Assertions,
) : IntegrationTestConfig() {

    @Test
    fun `should get user reservations`() {
        val user = given.user.exists().build()
        val (author, books) = given.author.exists().withBook().build()
        val library = given.library.exists().hasCopy(books[0].resourceId).build()
        val reservation = given.reservation.exists(user.userId, books[0].resourceId, library.libraryId)

        val response = reservationService.getUserReservations(user.userId)

        assertThat(response).containsExactly(
            ReservationHistory(library.libraryId, books[0].toBasicData(), author.toBasicData(), reservation.reservationPeriod.finishDate, ResourceType.BOOK)
        )
    }

    @Test
    fun `should get user reservations ordered by start of a reservation`() {
        val user = given.user.exists().build()
        val (author, books, ebooks) = given.author.exists().withBook().withEbook().build()
        val library = given.library.exists().hasCopy(books[0].resourceId).hasCopy(ebooks[0].resourceId).build()
        val reservation1 = given.reservation.exists(user.userId, books[0].resourceId, library.libraryId)
        val reservation2 = given.reservation.exists(user.userId, ebooks[0].resourceId, library.libraryId, clock.lastWeek())

        val response = reservationService.getUserReservations(user.userId)

        assertThat(response).containsExactly(
            ReservationHistory(library.libraryId, ebooks[0].toBasicData(), author.toBasicData(), reservation2.reservationPeriod.finishDate, ResourceType.EBOOK),
            ReservationHistory(library.libraryId, books[0].toBasicData(), author.toBasicData(), reservation1.reservationPeriod.finishDate, ResourceType.BOOK)
        )
    }

    @Test
    fun `should get short info about a reservation`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().withEbook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId, Availability(0)).build()
        val reservation = given.reservation.exists(user.userId, book.resourceId, library.libraryId)

        val response = reservationService.getReservationShortInfo(book.resourceId, user.userId)

        assertThat(response).isEqualTo(ReservationShortInfo(reservation.reservationPeriod.finishDate, library.libraryName))
    }

    @Test
    fun `should throw exception when getting short info about not existing reservation`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().withEbook().build().second[0]

        assertThrows<ReservationNotFoundException> { reservationService.getReservationShortInfo(book.resourceId, user.userId) }
    }

    @Test
    fun `should reserve a resource`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().withEbook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId, Availability(0)).build()

        reservationService.reserveResource(book.resourceId, library.libraryId, user.userId)

        assert.reservation.isSaved(book.resourceId, library.libraryId, user.userId)
    }

    @Test
    fun `should throw exception when trying to reserve a resource when more than 0 copies are available`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().withEbook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()

        assertThrows<CannotReserveResourceException> {
            reservationService.reserveResource(book.resourceId, library.libraryId, user.userId)
        }
    }

    @Test
    fun `should keep only the latest reservation of a resource`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().withEbook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId, Availability(0)).build()

        reservationService.reserveResource(book.resourceId, library.libraryId, user.userId)
        reservationService.reserveResource(book.resourceId, library.libraryId, user.userId)
        reservationService.reserveResource(book.resourceId, library.libraryId, user.userId)

        // checks also if exactly single record exists
        assert.reservation.isSaved(book.resourceId, library.libraryId, user.userId)
    }

    @Test
    fun `should delete a reservation`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().withEbook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        given.reservation.exists(user.userId, book.resourceId, library.libraryId)

        reservationService.deleteReservation(book.resourceId, user.userId)

        assert.reservation.isNotSaved(book.resourceId, user.userId)
    }
}
