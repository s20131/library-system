package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.RentalHistory
import pja.s20131.librarysystem.domain.resource.model.Rental
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId

interface RentalRepository {
    fun getAllBy(userId: UserId): List<RentalHistory>
    fun getLatest(resourceId: ResourceId, userId: UserId): Rental
    fun save(rental: Rental)
}
