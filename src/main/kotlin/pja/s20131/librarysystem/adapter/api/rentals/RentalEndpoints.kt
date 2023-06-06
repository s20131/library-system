package pja.s20131.librarysystem.adapter.api.rentals

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.RentalHistory
import pja.s20131.librarysystem.domain.resource.RentalService
import pja.s20131.librarysystem.domain.resource.RentalShortInfo
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.AuthService

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

    // TODO endpoint available for librarian only, with passed customer's code
    @PutMapping("/rentals/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun completeBorrowingBook(@PathVariable resourceId: ResourceId) {
        authService.withUserContext {
            rentalService.completeBookRental(resourceId, it)
        }
    }
}

private fun List<RentalHistory>.toResponse() =
    map { GetRentalHistoryResponse(it.libraryId, it.resource, it.author, it.startDate, it.rentalStatus, it.resourceType) }

private fun RentalShortInfo.toResponse() = GetRentalShortInfoResponse(rentalStatus, finish, library, penalty)
