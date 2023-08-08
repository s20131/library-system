package pja.s20131.librarysystem.domain.resource

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.library.port.LibraryRepository
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.FinishDate
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.resource.port.CopyRepository
import pja.s20131.librarysystem.domain.resource.port.ReservationRepository
import pja.s20131.librarysystem.domain.resource.port.ResourceRepository
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.exception.BaseException
import java.time.Clock

@Service
@Transactional
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val libraryRepository: LibraryRepository,
    private val copyRepository: CopyRepository,
    private val resourceRepository: ResourceRepository,
    private val clock: Clock,
) {

    fun getUserReservations(userId: UserId): List<ReservationHistory> {
        return reservationRepository.getAllBy(userId)
    }

    fun getReservationShortInfo(resourceId: ResourceId, userId: UserId): ReservationShortInfo {
        val reservation = reservationRepository.getCurrentlyActiveBy(resourceId, userId, clock.instant())
        val library = libraryRepository.get(reservation.libraryId)
        return ReservationShortInfo(reservation.reservationPeriod.finishDate, library.libraryName)
    }

    fun reserveResource(resourceId: ResourceId, libraryId: LibraryId, userId: UserId) {
        val available = copyRepository.getAvailability(resourceId, libraryId)
        if (available.value != 0) throw CannotReserveResourceException(resourceId, libraryId)
        val resource = resourceRepository.getResource(resourceId)
        val reservation = resource.reserve(userId, libraryId, clock.instant())
        reservationRepository.upsert(reservation)
    }

    fun deleteReservation(resourceId: ResourceId, userId: UserId) {
        reservationRepository.delete(resourceId, userId)
    }
}

data class ReservationHistory(
    val libraryId: LibraryId,
    val resource: ResourceBasicData,
    val author: AuthorBasicData,
    val finishDate: FinishDate,
    val resourceType: ResourceType,
)

data class ReservationShortInfo(
    val finish: FinishDate,
    val library: LibraryName,
)

class CannotReserveResourceException : BaseException {
    constructor(
        resourceId: ResourceId,
        libraryId: LibraryId,
    ) : super("Resource ${resourceId.value} cannot be reserved in ${libraryId.value} as it can be borrowed")

    constructor(resourceId: ResourceId) : super("Resource ${resourceId.value} is not of type ${ResourceType.BOOK} and cannot be reserved to borrow")
}
