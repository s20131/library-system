package pja.s20131.librarysystem.rental

import org.assertj.core.api.Assertions.assertThat
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.Penalty
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId

@Component
class RentalAssertions(
    private val rentalDatabaseHelper: RentalDatabaseHelper,
) {

    fun isSaved(
        userId: UserId,
        resourceId: ResourceId,
        libraryId: LibraryId,
        rentalPeriod: RentalPeriod,
        rentalStatus: RentalStatus,
        penalty: Penalty?
    ) {
        val expectedRental = rentalDatabaseHelper.findBy(resourceId, userId, libraryId)

        requireNotNull(expectedRental)
        assertThat(expectedRental.rentalPeriod).isEqualTo(rentalPeriod)
        assertThat(expectedRental.rentalStatus).isEqualTo(rentalStatus)
        assertThat(expectedRental.penalty).isEqualTo(penalty)
    }

    fun isSaved(
        userId: UserId,
        resourceId: ResourceId,
        libraryId: LibraryId,
    ) {
        val expectedRental = rentalDatabaseHelper.findBy(resourceId, userId, libraryId)

        requireNotNull(expectedRental)
    }
}
