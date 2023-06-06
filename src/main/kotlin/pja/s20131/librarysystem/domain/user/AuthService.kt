package pja.s20131.librarysystem.domain.user

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserRole
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.model.Username
import pja.s20131.librarysystem.domain.user.port.UserRepository
import pja.s20131.librarysystem.exception.BaseException
import pja.s20131.librarysystem.infrastructure.security.PrincipalConverter

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val principalConverter: PrincipalConverter,
) : AuthenticationProvider {

    //TODO should be extracted as custom provider?
    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.principal
        val password = authentication.credentials
        val user = userRepository.findBy(Username(username.toString()))
        if (user == null || !passwordEncoder.matches(password.toString(), user.password.value)) {
            throw BadCredentialsException()
        }
        return if (userRepository.isLibrarian(user.userId)) {
            UsernamePasswordAuthenticationToken(user.userId.value, password, listOf(SimpleGrantedAuthority(UserRole.LIBRARIAN.toString())))
        } else {
            UsernamePasswordAuthenticationToken(user.userId.value, password, emptyList())
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }

    fun register(dto: RegisterUserDto): UserId {
        validateUserData(dto)
        val user = User.from(dto).securePassword()
        userRepository.save(user)
        userRepository.saveSettings(user.userId, UserSettings.basic())
        return user.userId
    }

    // TODO inline?
    fun <T> withUserContext(body: (UserId) -> T): T {
        val userAuth = SecurityContextHolder.getContext().authentication
        val userId = principalConverter.convert(userAuth)
        return body.invoke(userId)
    }

    private fun validateUserData(dto: RegisterUserDto) {
        if (userRepository.findBy(dto.email) != null)
            throw EmailAlreadyExistsException(dto.email)
        dto.password.validate()
    }

    private fun User.securePassword() =
        copy(password = Password(passwordEncoder.encode(password.value)))
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
