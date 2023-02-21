package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.AuthorTable
import pja.s20131.librarysystem.domain.resource.port.AuthorRepository

@SpringBootTest
@Transactional
class AuthorRepositoryTests @Autowired constructor(
    val authorRepository: AuthorRepository,
    val resourceDatabaseHelper: ResourceDatabaseHelper,
) {
    @BeforeEach
    fun clear() {
        AuthorTable.deleteAll()
    }

    @Test
    fun `should correctly retrieve an author`() {
        val author = ResourceGen.author()
        resourceDatabaseHelper.insertAuthor(author)

        val retrievedAuthor = authorRepository.get(author.authorId)

        assertThat(retrievedAuthor).isEqualTo(author)
    }

    @Test
    fun `should correctly insert an author`() {
        val author = ResourceGen.author()

        authorRepository.insert(author)
        val retrievedAuthor = resourceDatabaseHelper.getAuthor(author.authorId)

        assertThat(retrievedAuthor).isEqualTo(author)
    }
}
