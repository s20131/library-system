package pja.s20131.librarysystem.domain.user.model

import java.util.UUID

data class User(
    val userId: UserId,
    val firstName: FirstName,
    val lastName: LastName,
    val email: Email,
    val login: Login,
    val password: Password,
) {
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
value class FirstName(val value: String)

@JvmInline
value class LastName(val value: String)

@JvmInline
value class Email(val value: String)

@JvmInline
value class Login(val value: String)

@JvmInline
value class Password(val value: String)
