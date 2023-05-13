package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.assertions.Assertions
import pja.s20131.librarysystem.domain.resource.AuthorService
import pja.s20131.librarysystem.Preconditions

@SpringBootTest
class AuthorServiceTests @Autowired constructor(
    private val authorService: AuthorService,
    private val assuming: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should correctly retrieve an author`() {
        val (author) = assuming.author.exists().build()

        val retrievedAuthor = authorService.getAuthor(author.authorId)

        assertThat(retrievedAuthor).isEqualTo(author)
    }

    @Test
    fun `should correctly insert an author`() {
        val command = ResourceGen.addAuthorCommand()

        val authorId = authorService.addAuthor(command)

        assert.author.isSaved(authorId)
    }
}
