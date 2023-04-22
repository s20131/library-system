package pja.s20131.librarysystem.domain.user.port

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.exceptions.BadRequestException
import pja.s20131.librarysystem.adapter.exceptions.ForbiddenException
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.Username

@Service
@Transactional
class AuthService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
): AuthenticationProvider {

    fun register(command: RegisterUserCommand) {
        validateUserData(command)
        val user = User(
            UserId.generate(),
            command.firstName,
            command.lastName,
            command.email,
            command.username,
            Password(passwordEncoder.encode(command.password.value)),
        )
        userRepository.insertUser(user)
    }

    //TODO should be extracted as custom provider?
    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.principal
        val password = authentication.credentials
        val user = userRepository.findBy(Username(username.toString()))
        if (user == null || !passwordEncoder.matches(password.toString(), user.password.value)) {
            throw BadCredentialsException()
        }
        return UsernamePasswordAuthenticationToken(
            user.userId.value, password, listOf()
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }

    private fun validateUserData(command: RegisterUserCommand) {
        if (userRepository.findBy(command.email) != null)
            throw EmailAlreadyExistsException(command.email)
        command.password.validate()
    }

}

data class RegisterUserCommand(
    val firstName: FirstName,
    val lastName: LastName,
    val email: Email,
    val username: Username,
    val password: Password,
)

data class Credentials(
    val username: Username,
    val password: Password,
)

class EmailAlreadyExistsException(email: Email) : BadRequestException("Email \"${email.value}\" is already in use")

class BadCredentialsException : ForbiddenException("Wrong username or password were provided")

