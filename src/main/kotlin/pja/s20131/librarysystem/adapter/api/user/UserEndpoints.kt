package pja.s20131.librarysystem.adapter.api.user

import java.security.Principal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.adapter.api.resource.resource.GetStoredResourceResponse
import pja.s20131.librarysystem.domain.resource.port.ResourceService
import pja.s20131.librarysystem.domain.resource.port.StoredResource
import pja.s20131.librarysystem.domain.user.model.UserBasicData
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.port.UserService
import pja.s20131.librarysystem.infrastructure.security.PrincipalConverter

@RestController
class UserEndpoints(
    private val userService: UserService,
    private val resourceService: ResourceService,
    private val principalConverter: PrincipalConverter,
) {
    // TODO add (endpoint) tests

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UserId): GetUserResponse {
        return userService.getUser(userId).toResponse()
    }

    @GetMapping("/{userId}/settings")
    fun getUserSettings(@PathVariable userId: UserId): GetUserSettingsResponse {
        return userService.getUserSettings(userId).toResponse()
    }

    @GetMapping("/storage")
    fun getUserStorage(principal: Principal): List<GetStoredResourceResponse> {
        return resourceService.getUserStorage(principalConverter.convert(principal)).toResponse()
    }

}

private fun UserBasicData.toResponse() = GetUserResponse(firstName, lastName, email)
private fun UserSettings.toResponse() = GetUserSettingsResponse(sendEndOfRentalReminder, sendWhenAvailableReminder, kindleEmail)
private fun List<StoredResource>.toResponse() = map { GetStoredResourceResponse(it.resource, it.author, it.resourceType, it.since) }

