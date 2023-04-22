package pja.s20131.librarysystem.domain.resource.port

import java.time.Instant
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.user.model.UserId

@Service
@Transactional
class ResourceService(
    private val resourceRepository: ResourceRepository,
) {

    fun getUserStorage(userId: UserId): List<StoredResource> {
        return resourceRepository.getFromStorageBy(userId)
    }
}

data class StoredResource(
    val resource: ResourceBasicData,
    val author: AuthorBasicData,
    val resourceType: ResourceType,
    val since: Instant,
)
