package pja.s20131.librarysystem.adapter.api.user

import java.security.Principal
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.adapter.api.resource.resource.GetStoredResourceResponse
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.port.ResourceService
import pja.s20131.librarysystem.domain.resource.port.StoredResource
import pja.s20131.librarysystem.infrastructure.security.PrincipalConverter

@RestController
@RequestMapping("/storage")
class StorageEndpoints(
    private val resourceService: ResourceService,
    private val principalConverter: PrincipalConverter,
) {

    @GetMapping
    fun getUserStorage(principal: Principal): List<GetStoredResourceResponse> {
        val userId = principalConverter.convert(principal)
        return resourceService.getUserStorage(userId).toResponse()
    }

    @GetMapping("/{resourceId}")
    fun getIsInUserStorage(@PathVariable resourceId: ResourceId, principal: Principal): Boolean {
        val userId = principalConverter.convert(principal)
        return resourceService.getIsInUserStorage(userId, resourceId)
    }

    @PostMapping("/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addToUserStorage(@PathVariable resourceId: ResourceId, principal: Principal) {
        val userId = principalConverter.convert(principal)
        resourceService.addToUserStorage(userId, resourceId)
    }

    @DeleteMapping("/{resourceId}")
    fun removeFromUserStorage(@PathVariable resourceId: ResourceId, principal: Principal) {
        val userId = principalConverter.convert(principal)
        resourceService.removeFromUserStorage(userId, resourceId)
    }
}

private fun List<StoredResource>.toResponse() = map { GetStoredResourceResponse(it.resource, it.author, it.resourceType, it.since) }
