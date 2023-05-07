package pja.s20131.librarysystem.ebook

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.adapter.database.resource.EbookNotFoundException
import pja.s20131.librarysystem.assertions.Assertions
import pja.s20131.librarysystem.domain.resource.EbookService
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.port.AuthorNotFoundException
import pja.s20131.librarysystem.preconditions.Preconditions

@SpringBootTest
class EbookServiceTests @Autowired constructor(
    private val ebookService: EbookService,
    private val preconditions: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should get all ebooks`() {
        val (author, _, ebooks) = preconditions.resource.authorExists().withEbook(series = DEFAULT_SERIES).withEbook().build()

        val response = ebookService.getAllEbooks()

        assertThat(response).containsExactly(
            ResourceWithAuthorBasicData(ebooks[0].toBasicData(), author.toBasicData()),
            ResourceWithAuthorBasicData(ebooks[1].toBasicData(), author.toBasicData()),
        )
    }

    // TODO how to compare?
    @Disabled("content comparison")
    @Test
    fun `should return an ebook`() {
        val ebook = preconditions.resource.authorExists().withEbook().build().third[0]

        val response = ebookService.getEbook(ebook.resourceId)

        assertThat(ebook).isEqualTo(response)
    }

    @Test
    fun `should throw an exception when getting an ebook which doesn't exist`() {
        val ebook = EbookGen.ebook()

        assertThrows<EbookNotFoundException> { ebookService.getEbook(ebook.resourceId) }
    }

    @Test
    fun `should correctly add an ebook`() {
        val (author) = preconditions.resource.authorExists().build()
        val series = preconditions.resource.seriesExists()
        val command = EbookGen.addEbookCommand(authorId = author.authorId, series = series)

        val ebookId = ebookService.addEbook(command)

        assert.ebook.isSaved(ebookId)
    }

    @Test
    fun `should throw an exception when adding an ebook with not existing author id`() {
        val command = EbookGen.addEbookCommand()

        assertThrows<AuthorNotFoundException> { ebookService.addEbook(command) }
    }

    companion object {
        private val DEFAULT_SERIES = Series("series")
    }
}
