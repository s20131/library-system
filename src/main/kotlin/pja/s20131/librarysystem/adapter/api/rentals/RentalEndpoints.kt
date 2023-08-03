package pja.s20131.librarysystem.adapter.api.rentals

import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.RentalHistory
import pja.s20131.librarysystem.domain.resource.RentalService
import pja.s20131.librarysystem.domain.resource.RentalShortInfo
import pja.s20131.librarysystem.domain.resource.model.BookBasicData
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.Rental
import pja.s20131.librarysystem.domain.resource.model.RentalStatusTransition
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.AuthService
import pja.s20131.librarysystem.domain.user.model.CardNumber

@RestController
class RentalEndpoints(
    private val rentalService: RentalService,
    private val authService: AuthService,
) {

    @GetMapping("/rentals")
    fun getUserRentals(): List<GetRentalHistoryResponse> {
        return authService.withUserContext {
            rentalService.getUserRentals(it)
        }.toResponse()
    }

    @GetMapping("/rentals/{resourceId}")
    fun getLatestRentalShortInfo(@PathVariable resourceId: ResourceId): GetRentalShortInfoResponse {
        return authService.withUserContext {
            rentalService.getLatestRentalShortInfo(resourceId, it)
        }.toResponse()
    }

    @PostMapping("/libraries/{libraryId}/rentals/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun borrowResource(@PathVariable libraryId: LibraryId, @PathVariable resourceId: ResourceId) {
        authService.withUserContext {
            rentalService.borrowResource(resourceId, libraryId, it)
        }
    }

    @PostMapping("/libraries/{libraryId}/librarian/rentals/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_LIBRARIAN")
    fun borrowResourceForCustomer(@PathVariable libraryId: LibraryId, @PathVariable isbn: ISBN, @RequestParam cardNumber: CardNumber) {
        authService.withUserContext {
            rentalService.borrowResourceForCustomer(libraryId, isbn, cardNumber, it)
        }
    }

    @GetMapping("/libraries/{libraryId}/librarian/rentals")
    @Secured("ROLE_LIBRARIAN")
    fun getCustomerAwaitingBooks(@PathVariable libraryId: LibraryId, @RequestParam cardNumber: CardNumber): List<GetBookBasicDataResponse> {
        return authService.withUserContext {
            rentalService.getCustomerAwaitingBooks(libraryId, cardNumber)
        }.toResponse()
    }

    @GetMapping("/libraries/{libraryId}/librarian/rentals/{isbn}/status")
    @Secured("ROLE_LIBRARIAN")
    fun checkBeforeReturningBook(
        @PathVariable libraryId: LibraryId,
        @PathVariable isbn: ISBN,
        @RequestParam cardNumber: CardNumber
    ): GetRentalPenaltyInfoResponse {
        return authService.withUserContext {
            rentalService.checkBeforeReturningBook(libraryId, isbn, cardNumber, it)
        }.toResponse()
    }

    @PutMapping("/libraries/{libraryId}/librarian/rentals/{isbn}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_LIBRARIAN")
    fun changeRentalStatus(
        @PathVariable libraryId: LibraryId,
        @PathVariable isbn: ISBN,
        // START, PROLONG, FINISH, PAY_OFF, CANCEL
        @RequestParam statusStrategy: RentalStatusTransition,
        @RequestParam cardNumber: CardNumber
    ) {
        authService.withUserContext {
            rentalService.changeRentalStatus(libraryId, isbn, statusStrategy, cardNumber, it)
        }
    }
}

private fun List<RentalHistory>.toResponse() =
    map { GetRentalHistoryResponse(it.libraryId, it.resource, it.author, it.startDate, it.rentalStatus, it.resourceType) }

private fun RentalShortInfo.toResponse() = GetRentalShortInfoResponse(rentalStatus, finish, library, penalty)

private fun Rental.toResponse() = GetRentalPenaltyInfoResponse(rentalPeriod.finishTime, rentalStatus, penalty)

@JvmName("toBookBasicDataResponse")
private fun List<BookBasicData>.toResponse() = map { GetBookBasicDataResponse(it.id, it.title, it.isbn) }
