package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.resource.RentalShortInfo
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
    fun completeBookRental(instant: Instant): Rental  {
        val rental = changeStatus(RentalStatusTransition.START)
        val updatedRental = rental.copy(rentalPeriod = RentalPeriod.startRental(instant))
        rentalPeriod.validateIsOverlapped(updatedRental.rentalPeriod, resourceId)
        return updatedRental
    }

    // TODO bug: borrow a book -> return -> borrow again
    fun validateCanBeBorrowed(previous: Rental?) {
        if (previous != null && rentalPeriod.isOverlapped(previous.rentalPeriod)) {
            throw RentalPeriodOverlappingDatesException(resourceId)
        }
        // TODO check if there is ANY not paid-off rental
        if (previous != null && previous.rentalStatus === RentalStatus.PROLONGED) {
            throw RentalNotPaidOffException(resourceId)
        }
    }

    fun validateCanBeDownloaded() {
        if (rentalStatus !== RentalStatus.ACTIVE) {
            throw RentalCannotBeDownloadedException(resourceId)
        }
    }

    fun validateIsActive() {
        if (rentalStatus !in RentalStatus.activeStatuses) {
            throw RentalNotActiveException(rentalId)
        }
    }

    fun changeStatus(transition: RentalStatusTransition): Rental {
        if (rentalStatus != transition.source) {
            throw RentalStatusCannotBeChangedException(this, transition.target)
        }
        return copy(rentalStatus = transition.target)
    }

    fun toShortInfo(libraryName: LibraryName) = RentalShortInfo(rentalStatus, rentalPeriod.finishTime, libraryName, penalty)
}

@JvmInline
value class RentalId(val value: UUID) {
    companion object {
        fun generate() = RentalId(UUID.randomUUID())
    }
}

enum class RentalStatus {
    ACTIVE, RESERVED_TO_BORROW, PROLONGED, CANCELLED, FINISHED;

    companion object {
        val activeStatuses = listOf(ACTIVE, PROLONGED, RESERVED_TO_BORROW)
    }
}

enum class RentalStatusTransition(val source: RentalStatus, val target: RentalStatus) {
    START(RentalStatus.RESERVED_TO_BORROW, RentalStatus.ACTIVE),
    PROLONG(RentalStatus.ACTIVE, RentalStatus.PROLONGED),
    FINISH(RentalStatus.ACTIVE, RentalStatus.FINISHED),
    PAY_OFF(RentalStatus.PROLONGED, RentalStatus.FINISHED),
    CANCEL(RentalStatus.RESERVED_TO_BORROW, RentalStatus.CANCELLED),
}

@JvmInline
value class Penalty(val value: BigDecimal)

data class RentalPeriod(
    val start: Instant,
    val finish: Instant,
) {
    val startDate = StartDate(LocalDate.ofInstant(start, ZoneId.of("Europe/Warsaw")))
    val finishTime = FinishTime(LocalDateTime.ofInstant(finish, ZoneId.of("Europe/Warsaw")))

    fun isOverlapped(other: RentalPeriod): Boolean =
        start.isBefore(other.finish) && other.start.isBefore(finish)

    fun validateIsOverlapped(other: RentalPeriod, resourceId: ResourceId) {
        if (!isOverlapped(other)) {
            throw RentalPeriodNotOverlappingDatesException(resourceId)
        }
    }

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

class RentalStatusCannotBeChangedException(rental: Rental, targetStatus: RentalStatus) :
    BaseException("Cannot change rental ${rental.rentalId.value} status from ${rental.rentalStatus} to $targetStatus")

class RentalNotActiveException(rentalId: RentalId) : BaseException("Rental $rentalId is not active")

class RentalNotPaidOffException(resourceId: ResourceId) :
    BaseException("Resource ${resourceId.value} cannot be borrowed as you haven't paid off the last rental of this resource")

class RentalCannotBeDownloadedException(resourceId: ResourceId) :
    BaseException("Resource ${resourceId.value} cannot be downloaded as you don't have an active rental")
