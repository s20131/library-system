package pja.s20131.librarysystem.api.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.user.UserBasicData
import pja.s20131.librarysystem.domain.user.UserId
import pja.s20131.librarysystem.domain.user.UserService
import pja.s20131.librarysystem.domain.user.UserSettings

@RestController
@RequestMapping("/users")
class UserEndpoints(
    val userService: UserService
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: UserId): GetUserResponse {
        return userService.getUser(id).toResponse()
    }

    @GetMapping("/{id}/user-settings")
    fun getUserSettings(@PathVariable id: UserId): GetUserSettingsResponse {
        return userService.getUserSettings(id).toResponse()
    }

}

private fun UserBasicData.toResponse() = GetUserResponse(firstName, lastName, email)

private fun UserSettings.toResponse() = GetUserSettingsResponse(sendEndOfRentalReminder, sendWhenAvailableReminder, kindleEmail)