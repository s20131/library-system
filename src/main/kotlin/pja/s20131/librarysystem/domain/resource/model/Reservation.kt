package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.user.model.UserId
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

data class Reservation(
    val userId: UserId,
    val resourceId: ResourceId,
    val libraryId: LibraryId,
    val reservationPeriod: ReservationPeriod,
)

data class ReservationPeriod(
    val start: Instant,
    val finish: Instant,
) {
    val finishDate = FinishDate(LocalDate.ofInstant(finish, ZoneId.of("Europe/Warsaw")))

    companion object {
        // TODO amount of days?
        fun startReservation(instant: Instant) = ReservationPeriod(instant, instant.plus(30, ChronoUnit.DAYS))
    }
}

@JvmInline
value class FinishDate(val value: LocalDate)
