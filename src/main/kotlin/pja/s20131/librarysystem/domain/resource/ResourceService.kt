package pja.s20131.librarysystem.domain.resource

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.resource.model.ResourceCover
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.port.ResourceRepository

@Service
@Transactional
class ResourceService(
    private val resourceRepository: ResourceRepository,
) {

    fun getResourceCover(resourceId: ResourceId): ResourceCover {
        return resourceRepository.getCover(resourceId)
    }
}
