package pja.s20131.librarysystem.adapter.api.reservations

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.ReservationHistory
import pja.s20131.librarysystem.domain.resource.ReservationService
import pja.s20131.librarysystem.domain.resource.ReservationShortInfo
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.infrastructure.security.PrincipalConverter
import java.security.Principal

@RestController
class ReservationEndpoints(
    private val reservationService: ReservationService,
    private val principalConverter: PrincipalConverter,
) {

    @GetMapping("/reservations")
    fun getUserReservations(principal: Principal): List<GetReservationHistoryResponse> {
        val userId = principalConverter.convert(principal)
        return reservationService.getUserReservations(userId).toResponse()
    }

    @GetMapping("/reservations/{resourceId}")
    fun getReservationShortInfo(@PathVariable resourceId: ResourceId, principal: Principal): GetReservationShortInfoResponse {
        val userId = principalConverter.convert(principal)
        return reservationService.getReserVationShortInfo(resourceId, userId).toResponse()
    }

    @PostMapping("/libraries/{libraryId}/reservations/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun reserveResource(@PathVariable resourceId: ResourceId, @PathVariable libraryId: LibraryId, principal: Principal) {
        val userId = principalConverter.convert(principal)
        reservationService.reserveResource(resourceId, libraryId, userId)
    }

    @DeleteMapping("/reservations/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteReservation(@PathVariable resourceId: ResourceId, principal: Principal) {
        val userId = principalConverter.convert(principal)
        reservationService.deleteReservation(resourceId, userId)
    }
}

private fun List<ReservationHistory>.toResponse() = map { GetReservationHistoryResponse(it.libraryId, it.resource, it.author, it.finishDate, it.resourceType) }

private fun ReservationShortInfo.toResponse() = GetReservationShortInfoResponse(finish, library)
