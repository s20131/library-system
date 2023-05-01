package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.ResourceCopy

interface CopyRepository {
    fun getAllBy(resourceId: ResourceId): List<ResourceCopy>
}
