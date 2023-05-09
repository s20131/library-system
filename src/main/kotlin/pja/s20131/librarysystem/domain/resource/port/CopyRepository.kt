package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.library.ResourceCopy
import pja.s20131.librarysystem.domain.resource.model.ResourceId

interface CopyRepository {
    fun getAllBy(resourceId: ResourceId): List<ResourceCopy>
}
