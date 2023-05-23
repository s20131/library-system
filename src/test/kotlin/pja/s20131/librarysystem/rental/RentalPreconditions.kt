package pja.s20131.librarysystem.rental

import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.Rental
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId

@Component
class RentalPreconditions(
    private val rentalDatabaseHelper: RentalDatabaseHelper,
) {

    fun exists(
        userId: UserId,
        resourceId: ResourceId,
        libraryId: LibraryId,
        rentalPeriod: RentalPeriod,
        rentalStatus: RentalStatus = RentalStatus.ACTIVE,
    ): Rental {
        return rentalDatabaseHelper.insertRental(userId, resourceId, libraryId, rentalPeriod, rentalStatus)
    }
}
