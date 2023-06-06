package pja.s20131.librarysystem.domain.user.model

import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.person.Person
import pja.s20131.librarysystem.domain.user.RegisterUserDto
import java.util.UUID
import pja.s20131.librarysystem.exception.BaseException

data class User(
    val userId: UserId,
    override val firstName: FirstName,
    override val lastName: LastName,
    val email: Email,
    val username: Username,
    val password: Password,
) : Person {
    fun toBasicData() = UserBasicData(firstName, lastName, email)

    companion object {
        fun from(dto: RegisterUserDto) = User(UserId.generate(), dto.firstName, dto.lastName, dto.email, dto.username, dto.password)
    }
}

data class UserBasicData(
    val firstName: FirstName,
    val lastName: LastName,
    val email: Email,
)

@JvmInline
value class UserId(val value: UUID) {
    companion object {
        fun generate() = UserId(UUID.randomUUID())
    }
}

@JvmInline
value class Email(val value: String)

@JvmInline
value class Username(val value: String)

@JvmInline
value class Password(val value: String) {

    private fun hasMin8Characters() = value.length >= 8

    fun validate() {
        when {
            hasMin8Characters().not() -> throw PasswordTooShortException()
        }
    }
}

enum class UserRole {
    LIBRARIAN
}

class PasswordTooShortException : BaseException("Password is too short")
