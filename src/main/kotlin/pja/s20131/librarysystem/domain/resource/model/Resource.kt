package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.exception.BaseException
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

sealed class Resource {
    abstract val resourceId: ResourceId
    abstract val title: Title
    abstract val authorId: AuthorId
    abstract val releaseDate: ReleaseDate
    abstract val description: Description?
    abstract val series: Series?
    abstract val status: ResourceStatus

    fun toBasicData(): ResourceBasicData = ResourceBasicData(resourceId, title)

    fun borrow(userId: UserId, libraryId: LibraryId, instant: Instant): Rental =
        Rental(RentalId.generate(), userId, resourceId, libraryId, RentalPeriod.startRental(instant), RentalStatus.ACTIVE, penalty = null)

    fun reserveToBorrow(userId: UserId, libraryId: LibraryId, instant: Instant): Rental {
        return when (this) {
            is Book -> Rental(
                RentalId.generate(),
                userId,
                resourceId,
                libraryId,
                RentalPeriod.startReservationToBorrow(instant),
                RentalStatus.RESERVED_TO_BORROW,
                penalty = null
            )

            else -> throw CannotBeReservedToBorrowException(resourceId)
        }
    }

    fun reserve(userId: UserId, libraryId: LibraryId, instant: Instant): Reservation =
        Reservation(userId, resourceId, libraryId, ReservationPeriod.startReservation(instant))
}

@JvmInline
value class ResourceId(val value: UUID) {
    companion object {
        fun generate() = ResourceId(UUID.randomUUID())
    }
}

@JvmInline
value class Title(val value: String)

@JvmInline
value class ReleaseDate(val value: LocalDate)

@JvmInline
value class Description(val value: String)

@JvmInline
value class Series(val value: String)

@JvmInline
value class Available(val value: Int)

enum class ResourceStatus {
    AVAILABLE, WITHDRAWN
}

enum class ResourceType {
    BOOK, EBOOK
}

data class ResourceBasicData(
    val id: ResourceId,
    val title: Title,
)

class CannotBeReservedToBorrowException(resourceId: ResourceId) :
    BaseException("Resource ${resourceId.value} is not of type ${ResourceType.BOOK} and cannot be reserved to borrow")
