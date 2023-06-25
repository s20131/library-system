package pja.s20131.librarysystem.domain.resource

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.resource.port.StorageRepository
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.port.UserRepository
import java.time.Clock
import java.time.Instant

@Service
@Transactional
class StorageService(
    private val storageRepository: StorageRepository,
    private val userRepository: UserRepository,
    private val clock: Clock,
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
        val user = userRepository.getBy(userId)
        storageRepository.add(user.userId, resourceId, clock.instant())
    }

    fun removeFromUserStorage(userId: UserId, resourceId: ResourceId) {
        val user = userRepository.getBy(userId)
        storageRepository.remove(user.userId, resourceId)
    }
}

data class StoredResource(
    val resource: ResourceBasicData,
    val author: AuthorBasicData,
    val resourceType: ResourceType,
    val since: Instant,
)
