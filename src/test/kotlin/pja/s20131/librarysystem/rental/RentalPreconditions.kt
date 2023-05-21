package pja.s20131.librarysystem.rental

import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.Rental
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.infrastracture.TestClock
import java.time.Instant

@Component
class RentalPreconditions(
    private val rentalDatabaseHelper: RentalDatabaseHelper,
    private val clock: TestClock,
) {

    fun exists(userId: UserId, resourceId: ResourceId, libraryId: LibraryId, instant: Instant = clock.now()): Rental {
        return rentalDatabaseHelper.insertRental(userId, resourceId, libraryId, instant)
    }
}
