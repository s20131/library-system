package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.model.Resource
import pja.s20131.librarysystem.domain.resource.model.ResourceId

interface ResourceRepository {
    fun getResource(resourceId: ResourceId): Resource
}
