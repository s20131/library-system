package pja.s20131.librarysystem.domain.user.port

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.exceptions.DomainException
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.Login
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.UserBasicData
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings

@Service
@Transactional
class UserService(
    val userRepository: UserRepository,
    val registrationService: RegistrationService,
) {
    fun addUser(addUserCommand: AddUserCommand) {
        if (userRepository.findBy(addUserCommand.email) != null)
            throw EmailAlreadyExistsException(addUserCommand.email)
        addUserCommand.password.validate()

        registrationService.register(addUserCommand)
    }

    fun getUser(userId: UserId): UserBasicData {
        return userRepository.get(userId)
    }

    fun getUserSettings(userId: UserId): UserSettings {
        return userRepository.getSettings(userId)
    }

}

data class AddUserCommand(
    val firstName: FirstName,
    val lastName: LastName,
    val email: Email,
    val login: Login,
    val password: Password,
)

class EmailAlreadyExistsException(email: Email) : DomainException("Email \"${email.value}\" is already in use")
