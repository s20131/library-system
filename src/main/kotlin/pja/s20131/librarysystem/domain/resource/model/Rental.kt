package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.RentalHistory
import pja.s20131.librarysystem.domain.user.model.UserId
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

data class Rental(
    val userId: UserId,
    val resourceId: ResourceId,
    val libraryId: LibraryId,
    val rentalPeriod: RentalPeriod,
    val rentalStatus: RentalStatus,
    val penalty: Penalty?,
)

enum class RentalStatus {
    ACTIVE, RESERVED_TO_BORROW, PROLONGED, PAID_OFF, CANCELED
}

@JvmInline
value class Penalty(val value: BigDecimal)

data class RentalPeriod(
    val start: Instant,
    val finish: Instant,
) {
    val startDate = StartDate(LocalDate.ofInstant(start, ZoneId.of("Europe/Warsaw")))

    companion object {
        fun startRental(instant: Instant) = RentalPeriod(instant, instant.plus(14, ChronoUnit.DAYS))

        fun startReservationToBorrow(instant: Instant) = RentalPeriod(instant, instant.plus(2, ChronoUnit.DAYS))
    }
}

@JvmInline
value class StartDate(val value: LocalDate)
