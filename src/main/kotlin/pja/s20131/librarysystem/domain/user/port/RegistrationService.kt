package pja.s20131.librarysystem.domain.user.port

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId

@Service
class RegistrationService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
) {

    fun register(addUserCommand: AddUserCommand) {
        val user = User(
            UserId.generate(),
            addUserCommand.firstName,
            addUserCommand.lastName,
            addUserCommand.email,
            addUserCommand.login,
            Password(passwordEncoder.encode(addUserCommand.password.value)),
        )
        userRepository.insertUser(user)
    }

}
