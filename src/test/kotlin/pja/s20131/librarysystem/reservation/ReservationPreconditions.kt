package pja.s20131.librarysystem.reservation

import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.Reservation
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.infrastracture.TestClock
import java.time.Instant

@Component
class ReservationPreconditions(
    private val reservationDatabaseHelper: ReservationDatabaseHelper,
    private val clock: TestClock,
) {
    fun exists(userId: UserId, resourceId: ResourceId, libraryId: LibraryId, instant: Instant = clock.now()): Reservation {
        return reservationDatabaseHelper.insertReservation(userId, resourceId, libraryId, instant)
    }
}
