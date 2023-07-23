package pja.s20131.librarysystem.author

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.IntegrationTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.domain.resource.AuthorService
import pja.s20131.librarysystem.resource.ResourceGen

@SpringBootTest
class AuthorServiceTests @Autowired constructor(
    private val authorService: AuthorService,
    private val given: Preconditions,
    private val assert: Assertions,
) : IntegrationTestConfig() {

    @Test
    fun `should correctly retrieve an author`() {
        val (author) = given.author.exists().build()

        val retrievedAuthor = authorService.getAuthor(author.authorId)

        assertThat(retrievedAuthor).isEqualTo(author)
    }

    @Test
    fun `should correctly insert an author`() {
        val dto = ResourceGen.addAuthorDto()

        val authorId = authorService.addAuthor(dto)

        assert.author.isSaved(authorId)
    }
}
