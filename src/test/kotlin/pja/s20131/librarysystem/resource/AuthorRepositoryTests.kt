package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.adapter.database.resource.AuthorTable
import pja.s20131.librarysystem.domain.resource.port.AuthorRepository

@SpringBootTest
class AuthorRepositoryTests @Autowired constructor(
    val authorRepository: AuthorRepository,
) {
    @BeforeEach
    fun clear() {
        transaction {
            AuthorTable.deleteAll()
        }
    }

    @Test
    fun `should correctly insert and retrieve an author`() {
        val author = author()
        transaction { authorRepository.insert(author) }

        val retrievedAuthor = transaction { authorRepository.get(author.authorId) }
        assertThat(retrievedAuthor).isEqualTo(author)
    }
}
