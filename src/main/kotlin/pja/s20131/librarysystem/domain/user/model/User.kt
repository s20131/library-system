package pja.s20131.librarysystem.domain.user.model

import pja.s20131.librarysystem.domain.exceptions.DomainException
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.person.Person
import java.util.UUID

data class User(
    val userId: UserId,
    override val firstName: FirstName,
    override val lastName: LastName,
    val email: Email,
    val username: Username,
    val password: Password,
) : Person {
    fun toBasicData() = UserBasicData(firstName, lastName, email)
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

    class PasswordTooShortException : DomainException("Password is too short")
}
