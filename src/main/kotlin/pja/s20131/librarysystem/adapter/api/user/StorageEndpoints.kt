package pja.s20131.librarysystem.adapter.api.user

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.adapter.api.resource.resource.GetStoredResourceResponse
import pja.s20131.librarysystem.domain.resource.StorageService
import pja.s20131.librarysystem.domain.resource.StoredResource
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.AuthService

@RestController
@RequestMapping("/storage")
class StorageEndpoints(
    private val storageService: StorageService,
    private val authService: AuthService,
) {

    @GetMapping
    fun getUserStorage(): List<GetStoredResourceResponse> {
        return authService.withUserContext {
            storageService.getUserStorage(it)
        }.toResponse()
    }

    @GetMapping("/{resourceId}")
    fun getIsInUserStorage(@PathVariable resourceId: ResourceId): Boolean {
        return authService.withUserContext {
            storageService.getIsInUserStorage(it, resourceId)
        }
    }

    @PostMapping("/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addToUserStorage(@PathVariable resourceId: ResourceId) {
        authService.withUserContext {
            storageService.addToUserStorage(it, resourceId)
        }
    }

    @DeleteMapping("/{resourceId}")
    fun removeFromUserStorage(@PathVariable resourceId: ResourceId) {
        authService.withUserContext {
            storageService.removeFromUserStorage(it, resourceId)
        }
    }
}

private fun List<StoredResource>.toResponse() = map { GetStoredResourceResponse(it.resource, it.author, it.resourceType, it.since) }
