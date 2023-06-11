package pja.s20131.librarysystem.domain.user

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.library.port.LibrarianRepository
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserRole
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.model.Username
import pja.s20131.librarysystem.domain.user.port.LibraryCardRepository
import pja.s20131.librarysystem.domain.user.port.UserRepository
import pja.s20131.librarysystem.exception.BaseException
import java.util.UUID

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val librarianRepository: LibrarianRepository,
    private val libraryCardRepository: LibraryCardRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    //TODO should be extracted as custom provider?
    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.principal
        val password = authentication.credentials
        val user = userRepository.findBy(Username(username.toString()))
        if (user == null || !passwordEncoder.matches(password.toString(), user.password.value)) {
            throw BadCredentialsException()
        }
        val roles = mutableListOf<SimpleGrantedAuthority>()
        if (librarianRepository.isLibrarian(user.userId)) {
            roles.add(SimpleGrantedAuthority(UserRole.LIBRARIAN.toString()))
        }
        return UsernamePasswordAuthenticationToken(user.userId.value, password, roles)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }

    fun register(dto: RegisterUserDto): UserId {
        validateUserData(dto)
        val user = User.from(dto).securePassword()
        userRepository.save(user)
        userRepository.saveSettings(user.userId, UserSettings.basic())
        // TODO pass domain object containing all data
        libraryCardRepository.save(user.userId)
        return user.userId.also {
            logger.info("New user ${user.username.value} has just registered")
        }
    }

    final inline fun <T> withUserContext(body: (UserId) -> T): T {
        val userAuth = SecurityContextHolder.getContext().authentication
        val userId = UserId(UUID.fromString(userAuth.name))
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
