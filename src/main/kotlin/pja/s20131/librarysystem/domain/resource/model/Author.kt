package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.person.Person
import java.util.UUID

data class Author(
    val authorId: AuthorId,
    override val firstName: FirstName,
    override val lastName: LastName,
) : Person {

    fun toBasicData(): AuthorBasicData = AuthorBasicData(firstName, lastName)

    companion object {
        fun from(firstName: FirstName, lastName: LastName) = Author(AuthorId.generate(), firstName, lastName)
    }
}

@JvmInline
value class AuthorId(val value: UUID) {
    companion object {
        fun generate() = AuthorId(UUID.randomUUID())
    }
}

data class AuthorBasicData(
    val firstName: FirstName,
    val lastName: LastName,
)
