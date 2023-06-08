package pja.s20131.librarysystem.domain.resource

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.library.port.LibrarianRepository
import pja.s20131.librarysystem.domain.library.port.LibraryRepository
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.FinishTime
import pja.s20131.librarysystem.domain.resource.model.Penalty
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.resource.model.StartDate
import pja.s20131.librarysystem.domain.resource.port.BookRepository
import pja.s20131.librarysystem.domain.resource.port.CopyRepository
import pja.s20131.librarysystem.domain.resource.port.RentalRepository
import pja.s20131.librarysystem.domain.resource.port.ResourceRepository
import pja.s20131.librarysystem.domain.user.model.CardNumber
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.port.LibraryCardRepository
import pja.s20131.librarysystem.exception.BaseException
import java.time.Clock

@Service
@Transactional
class RentalService(
    private val rentalRepository: RentalRepository,
    private val resourceRepository: ResourceRepository,
    private val bookRepository: BookRepository,
    private val copyRepository: CopyRepository,
    private val libraryRepository: LibraryRepository,
    private val libraryCardRepository: LibraryCardRepository,
    private val librarianRepository: LibrarianRepository,
    private val clock: Clock,
) {

    fun getUserRentals(userId: UserId): List<RentalHistory> {
        return rentalRepository.getAllBy(userId)
    }

    fun getLatestRentalShortInfo(resourceId: ResourceId, userId: UserId): RentalShortInfo {
        val latestRental = rentalRepository.getLatest(resourceId, userId)
        val library = libraryRepository.get(latestRental.libraryId)
        return RentalShortInfo(
            latestRental.rentalStatus,
            //TODO via config prop
            latestRental.rentalPeriod.finishTime,
            library.libraryName,
            latestRental.penalty,
        )
    }

    fun borrowResource(resourceId: ResourceId, libraryId: LibraryId, userId: UserId) {
        val available = copyRepository.getAvailability(resourceId, libraryId)
        if (available.value == 0) {
            throw InsufficientCopyAvailabilityException(resourceId, libraryId)
        }
        // TODO pass in the request and validate?
        val rental = when (val resource = resourceRepository.getResource(resourceId)) {
            is Book -> resource.reserveToBorrow(userId, libraryId, clock.instant())
            is Ebook -> resource.borrow(userId, libraryId, clock.instant())
        }
        val latest = rentalRepository.findLatest(resourceId, userId)
        rental.validateCanBeBorrowed(latest)
        rentalRepository.save(rental)
        copyRepository.decreaseAvailability(resourceId, libraryId)
    }

    fun getCustomerAwaitingBooks(libraryId: LibraryId, cardNumber: CardNumber): List<ResourceBasicData> {
        libraryCardRepository.getActive(cardNumber)
        return rentalRepository.getAllAwaitingBy(libraryId, cardNumber)
    }

    fun completeBookRental(resourceId: ResourceId, cardNumber: CardNumber, librarianId: UserId) {
        val book = bookRepository.get(resourceId)
        val libraryCard = libraryCardRepository.getActive(cardNumber)
            .also { it.checkIfActive() }
        val rental = rentalRepository.getLatest(book.resourceId, libraryCard.userId)
        if (!librarianRepository.isLibrarian(librarianId, rental.libraryId)) {
            throw UserNotPermittedToAccessLibraryException(librarianId, rental.libraryId)
        }
        val updatedRental = rental.completeBookRental(clock.instant())
        rental.validateIsOverlapped(updatedRental.rentalPeriod)
        rentalRepository.update(updatedRental)
    }
}

data class RentalHistory(
    val libraryId: LibraryId,
    val resource: ResourceBasicData,
    val author: AuthorBasicData,
    val startDate: StartDate,
    val rentalStatus: RentalStatus,
    val resourceType: ResourceType,
)

data class RentalShortInfo(
    val rentalStatus: RentalStatus,
    val finish: FinishTime,
    val library: LibraryName,
    val penalty: Penalty?,
)

class InsufficientCopyAvailabilityException(resourceId: ResourceId, libraryId: LibraryId) :
    BaseException("Resource ${resourceId.value} could not be borrowed from ${libraryId.value} for insufficient availability")

class UserNotPermittedToAccessLibraryException(librarianId: UserId, libraryId: LibraryId) :
    BaseException("Librarian ${librarianId.value} doesn't work at library ${libraryId.value}")
