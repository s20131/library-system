package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.RentalHistory
import pja.s20131.librarysystem.domain.resource.model.Rental
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.exception.BaseException

interface RentalRepository {
    fun getAllBy(userId: UserId): List<RentalHistory>
    fun getLatest(resourceId: ResourceId, userId: UserId): Rental = findLatest(resourceId, userId) ?: throw RentalNotFoundException(resourceId, userId)
    fun findLatest(resourceId: ResourceId, userId: UserId): Rental?
    fun save(rental: Rental)
    fun update(rental: Rental)
}

class RentalNotFoundException(resourceId: ResourceId, userId: UserId) :
    BaseException("Rental of resource ${resourceId.value} for user ${userId.value} was not found")
