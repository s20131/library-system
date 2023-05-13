package pja.s20131.librarysystem.ebook

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.adapter.database.resource.EbookNotFoundException
import pja.s20131.librarysystem.assertions.Assertions
import pja.s20131.librarysystem.domain.resource.EbookService
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData
import pja.s20131.librarysystem.domain.resource.port.AuthorNotFoundException
import pja.s20131.librarysystem.resource.ResourceGen

@SpringBootTest
class EbookServiceTests @Autowired constructor(
    private val ebookService: EbookService,
    private val assuming: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should get all ebooks`() {
        val (author, _, ebooks) = assuming.author.exists().withEbook(series = ResourceGen.defaultSeries).withEbook().build()

        val response = ebookService.getAllEbooks()

        assertThat(response).containsExactly(
            ResourceWithAuthorBasicData(ebooks[0].toBasicData(), author.toBasicData()),
            ResourceWithAuthorBasicData(ebooks[1].toBasicData(), author.toBasicData()),
        )
    }

    @Test
    fun `should return an ebook`() {
        val ebook = assuming.author.exists().withEbook().build().third[0]

        val response = ebookService.getEbook(ebook.resourceId)

        // not a good practice, but content has a byte array inside, which is hard to compare
        assertThat(ebook.copy(content = response.content)).isEqualTo(response)
    }

    @Test
    fun `should throw an exception when getting an ebook which doesn't exist`() {
        val ebook = EbookGen.ebook()

        assertThrows<EbookNotFoundException> { ebookService.getEbook(ebook.resourceId) }
    }

    @Test
    fun `should correctly add an ebook`() {
        val (author) = assuming.author.exists().build()
        val series = assuming.series.exists()
        val dto = EbookGen.addEbookDto(authorId = author.authorId, series = series)

        val ebookId = ebookService.addEbook(dto)

        assert.ebook.isSaved(ebookId)
    }

    @Test
    fun `should throw an exception when adding an ebook with not existing author id`() {
        val dto = EbookGen.addEbookDto()

        assertThrows<AuthorNotFoundException> { ebookService.addEbook(dto) }
    }
}
