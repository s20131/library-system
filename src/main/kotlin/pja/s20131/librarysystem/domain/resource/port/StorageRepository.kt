package pja.s20131.librarysystem.domain.resource.port

import java.time.Instant
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId

interface StorageRepository {
    fun getFromStorageBy(userId: UserId): List<StoredResource>
    fun getIsInUserStorage(userId: UserId, resourceId: ResourceId): Boolean
    fun addToUserStorage(userId: UserId, resourceId: ResourceId, since: Instant)
    fun removeFromUserStorage(userId: UserId, resourceId: ResourceId)
}
