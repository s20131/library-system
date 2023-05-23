package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.exception.BaseException
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID

data class Rental(
    val rentalId: RentalId,
    val userId: UserId,
    val resourceId: ResourceId,
    val libraryId: LibraryId,
    val rentalPeriod: RentalPeriod,
    val rentalStatus: RentalStatus,
    val penalty: Penalty?,
) {
    private fun isRentalPeriodOverlapped(other: Rental) =
        rentalPeriod.start.isBefore(other.rentalPeriod.finish) && other.rentalPeriod.start.isBefore(rentalPeriod.finish)

    fun completeBookRental(instant: Instant) = copy(rentalPeriod = RentalPeriod.startRental(instant), rentalStatus = RentalStatus.ACTIVE)

    fun validateIsOverlapped(other: Rental) {
        if (!isRentalPeriodOverlapped(other)) {
            throw RentalPeriodNotOverlappingDatesException(resourceId)
        }
    }

    fun validateIsNotOverlapped(other: Rental) {
        if (isRentalPeriodOverlapped(other)) {
            throw RentalPeriodOverlappingDatesException(resourceId)
        }
    }
}

@JvmInline
value class RentalId(val value: UUID) {
    companion object {
        fun generate() = RentalId(UUID.randomUUID())
    }
}

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
    val finishTime = FinishTime(LocalDateTime.ofInstant(finish, ZoneId.of("Europe/Warsaw")))

    companion object {
        fun startRental(instant: Instant) = RentalPeriod(instant, instant.plus(14, ChronoUnit.DAYS))

        fun startReservationToBorrow(instant: Instant) = RentalPeriod(instant, instant.plus(2, ChronoUnit.DAYS))
    }
}

@JvmInline
value class StartDate(val value: LocalDate)

@JvmInline
value class FinishTime(val value: LocalDateTime)

class RentalPeriodOverlappingDatesException(resourceId: ResourceId) :
    BaseException("Resource ${resourceId.value} cannot be borrowed as you already have an active rental of it")

class RentalPeriodNotOverlappingDatesException(resourceId: ResourceId) :
    BaseException("Resource ${resourceId.value} cannot be completed to be borrowed as reservation ended")
