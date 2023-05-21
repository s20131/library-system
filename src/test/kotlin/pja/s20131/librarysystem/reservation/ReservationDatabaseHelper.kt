package pja.s20131.librarysystem.reservation

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.adapter.database.resource.ReservationTable
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.Reservation
import pja.s20131.librarysystem.domain.resource.model.ReservationPeriod
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId
import java.time.Instant

@Component
class ReservationDatabaseHelper {

    fun insertReservation(
        userId: UserId,
        resourceId: ResourceId,
        libraryId: LibraryId,
        instant: Instant,
        reservationPeriod: ReservationPeriod = ReservationPeriod.startReservation(instant),
    ): Reservation {
        val reservation = Reservation(userId, resourceId, libraryId, reservationPeriod)
        transaction {
            ReservationTable.insert {
                it[ReservationTable.userId] = reservation.userId.value
                it[ReservationTable.resourceId] = reservation.resourceId.value
                it[ReservationTable.libraryId] = reservation.libraryId.value
                it[ReservationTable.start] = reservation.reservationPeriod.start
                it[ReservationTable.finish] = reservation.reservationPeriod.finish
            }
        }
        return reservation
    }
}
