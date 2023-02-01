package pja.s20131.librarysystem.domain.user

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
value class UserId(val raw: UUID) {
    companion object {
        fun generate() = UserId(UUID.randomUUID())
    }
}

@JvmInline
value class FirstName(val raw: String)

@JvmInline
value class LastName(val raw: String)

@JvmInline
value class Email(val raw: String)

@JvmInline
value class Login(val raw: String)

@JvmInline
value class Password(val raw: String)
