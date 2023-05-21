package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.ReservationHistory
import pja.s20131.librarysystem.domain.resource.model.Reservation
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId
import java.time.Instant

interface ReservationRepository {
    fun getAllBy(userId: UserId): List<ReservationHistory>
    fun getCurrentlyActiveBy(resourceId: ResourceId, userId: UserId, now: Instant): Reservation
    fun upsert(reservation: Reservation)
    fun delete(resourceId: ResourceId, userId: UserId)
}
