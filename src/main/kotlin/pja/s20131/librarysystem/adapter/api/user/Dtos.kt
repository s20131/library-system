package pja.s20131.librarysystem.adapter.api.user

import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.KindleEmail
import pja.s20131.librarysystem.domain.user.model.Login
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.SendEndOfRentalReminder
import pja.s20131.librarysystem.domain.user.model.SendWhenAvailableReminder
import pja.s20131.librarysystem.domain.user.port.AddUserCommand

data class AddUserRequest(
    val firstName: FirstName,
    val lastName: LastName,
    val email: Email,
    val login: Login,
    val password: Password,
) {
    fun toCommand() = AddUserCommand(firstName, lastName, email, login, password)
}

data class GetUserResponse(
    val firstName: FirstName,
    val lastName: LastName,
    val email: Email,
)

data class GetUserSettingsResponse(
    val sendEndOfRentalReminder: SendEndOfRentalReminder,
    val sendWhenAvailableReminder: SendWhenAvailableReminder,
    val kindleEmail: KindleEmail?,
)
