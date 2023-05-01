package pja.s20131.librarysystem.domain.resource.port

import java.time.Instant
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.StoredResource
import pja.s20131.librarysystem.domain.user.model.UserId

interface StorageRepository {
    fun getAllBy(userId: UserId): List<StoredResource>
    fun isInUserStorage(userId: UserId, resourceId: ResourceId): Boolean
    fun add(userId: UserId, resourceId: ResourceId, since: Instant)
    fun remove(userId: UserId, resourceId: ResourceId)
}
