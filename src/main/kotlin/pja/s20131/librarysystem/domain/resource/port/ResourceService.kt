package pja.s20131.librarysystem.domain.resource.port

import java.time.Instant
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.user.model.UserId

@Service
@Transactional
class ResourceService(
    private val storageRepository: StorageRepository,
    private val copyRepository: CopyRepository,
) {

    fun getUserStorage(userId: UserId): List<StoredResource> {
        return storageRepository.getAllBy(userId)
    }

    fun getIsInUserStorage(userId: UserId, resourceId: ResourceId): Boolean {
        return storageRepository.isInUserStorage(userId, resourceId)
    }

    fun addToUserStorage(userId: UserId, resourceId: ResourceId) {
        storageRepository.add(userId, resourceId, Instant.now())
    }

    fun removeFromUserStorage(userId: UserId, resourceId: ResourceId) {
        storageRepository.remove(userId, resourceId)
    }

    fun getResourceCopyInLibraries(resourceId: ResourceId): List<ResourceCopy> {
        return copyRepository.getAllBy(resourceId)
    }
}

data class StoredResource(
    val resource: ResourceBasicData,
    val author: AuthorBasicData,
    val resourceType: ResourceType,
    val since: Instant,
)

data class ResourceCopy(
    val libraryId: LibraryId,
    val available: Available,
)
