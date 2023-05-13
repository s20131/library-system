package pja.s20131.librarysystem.domain.user

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.model.Username
import pja.s20131.librarysystem.domain.user.port.UserRepository
import pja.s20131.librarysystem.exception.BaseException

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {

    fun register(dto: RegisterUserDto): UserId {
        validateUserData(dto)
        val user = User.from(dto).copy(password = Password(passwordEncoder.encode(dto.password.value)))
        userRepository.save(user)
        userRepository.saveSettings(user.userId, UserSettings.basic())
        return user.userId
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

    private fun validateUserData(dto: RegisterUserDto) {
        if (userRepository.findBy(dto.email) != null)
            throw EmailAlreadyExistsException(dto.email)
        dto.password.validate()
    }

}

data class RegisterUserDto(
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

class EmailAlreadyExistsException(email: Email) : BaseException("Email \"${email.value}\" is already in use")

class BadCredentialsException : BaseException("Wrong username or password")

