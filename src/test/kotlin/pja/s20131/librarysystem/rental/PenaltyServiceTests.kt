package pja.s20131.librarysystem.rental

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.domain.resource.PenaltyService
import pja.s20131.librarysystem.domain.resource.model.Penalty
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import java.math.BigDecimal

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
        val rental = given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.monthAgo()), penalty = Penalty(
            BigDecimal(2.50)))

        penaltyService.updatePenaltiesForResourceOverdue()

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, rental.rentalStatus, Penalty(BigDecimal("5.00")))
    }

    @Test
    fun `should calculate and set penalty if was null`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        val rental = given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.monthAgo()))

        penaltyService.updatePenaltiesForResourceOverdue()

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, rental.rentalStatus, Penalty(BigDecimal("2.50")))
    }
}
