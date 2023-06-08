package pja.s20131.librarysystem.adapter.api.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.user.AuthService
import pja.s20131.librarysystem.domain.user.UserService
import pja.s20131.librarysystem.domain.user.model.LibraryCard
import pja.s20131.librarysystem.domain.user.model.UserBasicData
import pja.s20131.librarysystem.domain.user.model.UserSettings

@RestController
class UserEndpoints(
    private val userService: UserService,
    private val authService: AuthService,
) {
    @GetMapping("/user")
    fun getUser(): GetUserResponse {
        return authService.withUserContext {
            userService.getUser(it)
        }.toResponse()
    }

    @GetMapping("/settings")
    fun getUserSettings(): GetUserSettingsResponse {
        return authService.withUserContext {
            userService.getUserSettings(it)
        }.toResponse()
    }

    @GetMapping("/card")
    fun getUserLibraryCard(): GetUserLibraryCardResponse {
        return authService.withUserContext {
            userService.getUserLibraryCard(it)
        }.toResponse()
    }
}

private fun UserBasicData.toResponse() = GetUserResponse(firstName, lastName, email)
private fun UserSettings.toResponse() = GetUserSettingsResponse(sendEndOfRentalReminder, sendWhenAvailableReminder, kindleEmail)
private fun LibraryCard.toResponse() = GetUserLibraryCardResponse(cardNumber, expiration)
