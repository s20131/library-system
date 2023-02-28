package pja.s20131.librarysystem.adapter.api.user

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.user.model.UserBasicData
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.port.UserService

@RestController
@RequestMapping("/users")
class UserEndpoints(
    val userService: UserService
) {
    // TODO add (endpoint) tests
    @PostMapping(consumes = ["application/json"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addUser(@RequestBody addUserRequest: AddUserRequest) {
        return userService.addUser(addUserRequest.toCommand())
    }

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
