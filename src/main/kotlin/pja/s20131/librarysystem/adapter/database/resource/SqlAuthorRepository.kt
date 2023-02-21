package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.exceptions.NotFoundException
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.port.AuthorRepository

@Repository
class SqlAuthorRepository : AuthorRepository {
    override fun get(authorId: AuthorId): Author {
        return AuthorTable
            .select { AuthorTable.id eq authorId.value }
            .singleOrNull()
            ?.toAuthor() ?: throw AuthorNotFound(authorId)
    }

    override fun insert(author: Author) {
        AuthorTable.insert {
            it[id] = author.authorId.value
            it[firstName] = author.firstName.value
            it[lastName] = author.lastName.value
        }
    }
}

internal fun ResultRow.toAuthor() = Author(
    AuthorId(this[AuthorTable.id].value),
    FirstName(this[AuthorTable.firstName]),
    LastName(this[AuthorTable.lastName]),
)

object AuthorTable : UUIDTable("author") {
    val firstName = text("first_name")
    val lastName = text("last_name")
}

class AuthorNotFound(authorId: AuthorId) : NotFoundException("Author with id=${authorId.value} doesn't exist")
