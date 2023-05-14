package pja.s20131.librarysystem.adapter.api.rentals

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.RentalHistory
import pja.s20131.librarysystem.domain.resource.RentalService
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.infrastructure.security.PrincipalConverter
import java.security.Principal

@RestController
class RentalEndpoints(
    private val rentalService: RentalService,
    private val principalConverter: PrincipalConverter,
) {

    @GetMapping("/rentals")
    fun getRentals(principal: Principal): List<GetRentalHistoryResponse> {
        val userId = principalConverter.convert(principal)
        return rentalService.getRentals(userId).toResponse()
    }

    @PostMapping("/libraries/{libraryId}/rentals/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun borrowResource(@PathVariable libraryId: LibraryId, @PathVariable resourceId: ResourceId, principal: Principal) {
        val userId = principalConverter.convert(principal)
        rentalService.borrowResource(resourceId, libraryId, userId)
    }
}

private fun List<RentalHistory>.toResponse() = map { GetRentalHistoryResponse(it.libraryId, it.resource, it.author, it.startDate, it.rentalStatus, it.resourceType) }
