package pja.s20131.librarysystem.rental

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.shaded.org.awaitility.Awaitility
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.domain.resource.model.Penalty
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import pja.s20131.librarysystem.domain.resource.utils.RentalJobService
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@SpringBootTest
@Sql("/sql/revoke_awaiting_resources.sql", "/sql/revoke_ebooks.sql")
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
    fun `should not change a resource status still waiting to be picked up`() {
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

    @Test
    fun `should run revoke ebooks procedure`() {
        val user = given.user.exists().build()
        val ebook = given.author.exists().withEbook().build().third[0]
        val library = given.library.exists().hasCopy(ebook.resourceId).build()
        val rental = given.rental.exists(
            user.userId,
            ebook.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.ACTIVE,
        )

        rentalJobService.revokeEbooks()

        assert.rental.isSaved(user.userId, ebook.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.FINISHED, penalty = null)
    }

    @Test
    fun `should run revoke ebooks procedure using cron`() {
        val user = given.user.exists().build()
        val ebook = given.author.exists().withEbook().build().third[0]
        val library = given.library.exists().hasCopy(ebook.resourceId).build()
        val rental = given.rental.exists(
            user.userId,
            ebook.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.ACTIVE,
        )

        Awaitility.await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted {
                assert.rental.isSaved(user.userId, ebook.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.FINISHED, penalty = null)
            }
    }

    @Test
    fun `should not revoke resources other than ebooks`() {
        val user = given.user.exists().build()
        val ebook = given.author.exists().withEbook().build().third[0]
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(ebook.resourceId).hasCopy(book.resourceId).build()
        val rental1 = given.rental.exists(
            user.userId,
            ebook.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.ACTIVE,
        )
        val rental2 = given.rental.exists(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.PROLONGED,
            Penalty(BigDecimal("2.50"))
        )

        rentalJobService.revokeEbooks()

        assert.rental.isSaved(user.userId, ebook.resourceId, library.libraryId, rental1.rentalPeriod, RentalStatus.FINISHED, penalty = null)
        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental2.rentalPeriod, RentalStatus.PROLONGED, Penalty(BigDecimal("2.50")))
    }

    @Test
    fun `should revoke multiple ebook resources`() {
        val user = given.user.exists().build()
        val ebook1 = given.author.exists().withEbook().build().third[0]
        val ebook2 = given.author.exists().withEbook().build().third[0]
        val ebook3 = given.author.exists().withEbook().build().third[0]
        val library = given.library.exists().hasCopy(ebook1.resourceId).hasCopy(ebook2.resourceId).hasCopy(ebook3.resourceId).build()
        val rental1 = given.rental.exists(
            user.userId,
            ebook1.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.ACTIVE,
        )
        val rental2 = given.rental.exists(
            user.userId,
            ebook2.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.ACTIVE,
        )
        val rental3 = given.rental.exists(
            user.userId,
            ebook3.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.yesterday()),
            RentalStatus.ACTIVE,
        )

        rentalJobService.revokeEbooks()

        assert.rental.isSaved(user.userId, ebook1.resourceId, library.libraryId, rental1.rentalPeriod, RentalStatus.FINISHED, penalty = null)
        assert.rental.isSaved(user.userId, ebook2.resourceId, library.libraryId, rental2.rentalPeriod, RentalStatus.FINISHED, penalty = null)
        assert.rental.isSaved(user.userId, ebook3.resourceId, library.libraryId, rental3.rentalPeriod, RentalStatus.ACTIVE, penalty = null)
    }
}
