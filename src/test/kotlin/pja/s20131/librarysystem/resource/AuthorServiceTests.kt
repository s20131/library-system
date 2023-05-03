package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.domain.resource.AuthorService

@SpringBootTest
class AuthorServiceTests @Autowired constructor(
    private val authorService: AuthorService,
    private val authorDatabaseHelper: AuthorDatabaseHelper,
) : BaseTestConfig() {

    @Test
    fun `should correctly retrieve an author`() {
        val author = ResourceGen.author()
        authorDatabaseHelper.insertAuthor(author)

        val retrievedAuthor = authorService.getAuthor(author.authorId)

        assertThat(retrievedAuthor).isEqualTo(author)
    }

    @Test
    fun `should correctly insert an author`() {
        val command = ResourceGen.addAuthorCommand()

        val authorId = authorService.addAuthor(command)

        authorDatabaseHelper.assertAuthorIsSaved(authorId)
    }
}