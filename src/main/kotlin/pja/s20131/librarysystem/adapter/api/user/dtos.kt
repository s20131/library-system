package pja.s20131.librarysystem.adapter.api.user

import com.fasterxml.jackson.annotation.JsonCreator
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.KindleEmail
import pja.s20131.librarysystem.domain.user.model.Username
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.SendEndOfRentalReminder
import pja.s20131.librarysystem.domain.user.model.SendWhenAvailableReminder
import pja.s20131.librarysystem.domain.user.Credentials
import pja.s20131.librarysystem.domain.user.RegisterUserDto

data class RegisterUserRequest(
    val firstName: FirstName,
    val lastName: LastName,
    val email: Email,
    val username: Username,
    val password: Password,
) {
    fun toDto(): RegisterUserDto = RegisterUserDto(firstName, lastName, email, username, password)

    companion object {
        @JvmStatic
        @JsonCreator
        fun creator(firstName: String, lastName: String, email: String, username: String, password: String) =
            RegisterUserRequest(FirstName(firstName), LastName(lastName), Email(email), Username(username), Password(password))
    }
}

data class AuthenticateUserRequest(
    val username: Username,
    val password: Password,
) {
    fun toCredentials(): Credentials = Credentials(username, password)
    
    companion object{
        @JvmStatic
        @JsonCreator
        fun creator(username: String, password: String) =
            AuthenticateUserRequest(Username(username), Password(password))
    }
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
