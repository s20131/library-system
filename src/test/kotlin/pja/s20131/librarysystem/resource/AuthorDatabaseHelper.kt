package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.AuthorTable
import pja.s20131.librarysystem.adapter.database.resource.toAuthor
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId

@Component
@Transactional
class AuthorDatabaseHelper {

    fun assertAuthorIsSaved(authorId: AuthorId) {
        val author = AuthorTable
            .select { AuthorTable.id eq authorId.value }
            .singleOrNull()
            ?.toAuthor()

        assertThat(author).isNotNull
        assertThat(author?.authorId).isEqualTo(authorId)
    }

    fun insertAuthor(author: Author) =
        AuthorTable.insert {
            it[id] = author.authorId.value
            it[firstName] = author.firstName.value
            it[lastName] = author.lastName.value
        }

}
