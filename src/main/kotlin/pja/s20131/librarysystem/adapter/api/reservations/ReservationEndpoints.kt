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
import pja.s20131.librarysystem.domain.user.AuthService

@RestController
class ReservationEndpoints(
    private val reservationService: ReservationService,
    private val authService: AuthService,
) {

    @GetMapping("/reservations")
    fun getUserReservations(): List<GetReservationHistoryResponse> {
        return authService.withUserContext {
            reservationService.getUserReservations(it)
        }.toResponse()
    }

    @GetMapping("/reservations/{resourceId}")
    fun getReservationShortInfo(@PathVariable resourceId: ResourceId): GetReservationShortInfoResponse {
        return authService.withUserContext {
            reservationService.getReservationShortInfo(resourceId, it)
        }.toResponse()
    }

    @PostMapping("/libraries/{libraryId}/reservations/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun reserveResource(@PathVariable resourceId: ResourceId, @PathVariable libraryId: LibraryId) {
        return authService.withUserContext {
            reservationService.reserveResource(resourceId, libraryId, it)
        }
    }

    @DeleteMapping("/reservations/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteReservation(@PathVariable resourceId: ResourceId) {
        return authService.withUserContext {
            reservationService.deleteReservation(resourceId, it)
        }
    }
}

private fun List<ReservationHistory>.toResponse() = map { GetReservationHistoryResponse(it.libraryId, it.resource, it.author, it.finishDate, it.resourceType) }
private fun ReservationShortInfo.toResponse() = GetReservationShortInfoResponse(finish, library)
