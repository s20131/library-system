package pja.s20131.librarysystem.rental

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.shaded.org.awaitility.Awaitility
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.domain.resource.utils.PenaltyService
import pja.s20131.librarysystem.domain.resource.model.Penalty
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@SpringBootTest
@Sql("/sql/update_penalties.sql")
class PenaltyServiceTests @Autowired constructor(
    private val penaltyService: PenaltyService,
    private val given: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should run update penalties procedure`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        val rental = given.rental.exists(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.PROLONGED,
            Penalty(BigDecimal(2.50)),
        )

        penaltyService.updatePenaltiesForResourceOverdue()

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, rental.rentalStatus, Penalty(BigDecimal("5.00")))
    }

    @Test
    fun `should update penalties for all existing and qualified rentals`() {
        val user = given.user.exists().build()
        val book1 = given.author.exists().withBook().build().second[0]
        val book2 = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book1.resourceId).hasCopy(book2.resourceId).build()
        val rental1 = given.rental.exists(
            user.userId,
            book1.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.PROLONGED,
            Penalty(BigDecimal(2.50)),
        )
        val rental2 = given.rental.exists(
            user.userId,
            book2.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.ACTIVE,
        )

        penaltyService.updatePenaltiesForResourceOverdue()

        assert.rental.isSaved(user.userId, book1.resourceId, library.libraryId, rental1.rentalPeriod, RentalStatus.PROLONGED, Penalty(BigDecimal("5.00")))
        assert.rental.isSaved(user.userId, book2.resourceId, library.libraryId, rental2.rentalPeriod, RentalStatus.PROLONGED, Penalty(BigDecimal("2.50")))
    }

    @Test
    fun `should run update penalties procedure using cron`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        val rental = given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.monthAgo()))

        Awaitility.await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted {
                assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.PROLONGED, Penalty(BigDecimal("2.50")))
            }
    }

    @Test
    fun `should calculate and set penalty if it was null and change rental status to prolonged`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        val rental = given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.monthAgo()))

        penaltyService.updatePenaltiesForResourceOverdue()

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.PROLONGED, Penalty(BigDecimal("2.50")))
    }

    @Test
    fun `shouldn't update penalties for ebook resources (if they were added manually for them)`() {
        val user = given.user.exists().build()
        val ebook = given.author.exists().withEbook().build().third[0]
        val library = given.library.exists().hasCopy(ebook.resourceId).build()
        val rental = given.rental.exists(
            user.userId,
            ebook.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.PROLONGED,
            Penalty(BigDecimal(2.50)),
        )

        penaltyService.updatePenaltiesForResourceOverdue()

        assert.rental.isSaved(user.userId, ebook.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.PROLONGED, Penalty(BigDecimal("2.50")))
    }
}
