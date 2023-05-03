package pja.s20131.librarysystem.domain.resource

import java.time.Instant
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.resource.port.CopyRepository
import pja.s20131.librarysystem.domain.resource.port.StorageRepository
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.port.UserRepository

@Service
@Transactional
class ResourceService(
    private val storageRepository: StorageRepository,
    private val userRepository: UserRepository,
    private val copyRepository: CopyRepository,
) {

    fun getUserStorage(userId: UserId): List<StoredResource> {
        val user = userRepository.getBy(userId)
        return storageRepository.getAllBy(user.userId)
    }

    fun getIsInUserStorage(userId: UserId, resourceId: ResourceId): Boolean {
        val user = userRepository.getBy(userId)
        return storageRepository.isInUserStorage(user.userId, resourceId)
    }

    fun addToUserStorage(userId: UserId, resourceId: ResourceId) {
        // TODO clock, inject time
        val user = userRepository.getBy(userId)
        storageRepository.add(user.userId, resourceId, Instant.now())
    }

    fun removeFromUserStorage(userId: UserId, resourceId: ResourceId) {
        val user = userRepository.getBy(userId)
        storageRepository.remove(user.userId, resourceId)
    }

    fun getResourceCopiesInLibraries(resourceId: ResourceId): List<ResourceCopy> {
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
