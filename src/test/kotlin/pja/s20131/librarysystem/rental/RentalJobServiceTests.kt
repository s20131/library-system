package pja.s20131.librarysystem.rental

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.shaded.org.awaitility.Awaitility
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import pja.s20131.librarysystem.domain.resource.utils.RentalJobService
import java.util.concurrent.TimeUnit

@SpringBootTest
@Sql("/sql/revoke_awaiting_resources.sql")
class RentalJobServiceTests @Autowired constructor(
    private val rentalJobService: RentalJobService,
    private val given: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should run revoke awaiting resources procedure`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        val rental = given.rental.exists(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.RESERVED_TO_BORROW,
        )

        rentalJobService.revokeAwaitingResources()

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.CANCELLED, penalty = null)
    }

    @Test
    fun `should run revoke awaiting resources procedure using cron`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        val rental = given.rental.exists(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.RESERVED_TO_BORROW,
        )

        Awaitility.await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted {
                assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.CANCELLED, penalty = null)
            }
    }

    @Test
    fun `should not change resources still waiting to be picked up`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        val rental = given.rental.exists(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod.startReservationToBorrow(clock.yesterday()),
            RentalStatus.RESERVED_TO_BORROW,
        )

        rentalJobService.revokeAwaitingResources()

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.RESERVED_TO_BORROW, penalty = null)
    }
}
