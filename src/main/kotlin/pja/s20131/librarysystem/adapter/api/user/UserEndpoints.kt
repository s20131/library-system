package pja.s20131.librarysystem.adapter.api.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.user.model.UserBasicData
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.port.UserService

@RestController
class UserEndpoints(
    private val userService: UserService,
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

}

private fun UserBasicData.toResponse() = GetUserResponse(firstName, lastName, email)
private fun UserSettings.toResponse() = GetUserSettingsResponse(sendEndOfRentalReminder, sendWhenAvailableReminder, kindleEmail)
