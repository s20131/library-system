package pja.s20131.librarysystem.ebook

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.adapter.database.resource.EbookNotFoundException
import pja.s20131.librarysystem.domain.resource.EbookService
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.port.AuthorNotFoundException
import pja.s20131.librarysystem.resource.AuthorDatabaseHelper
import pja.s20131.librarysystem.resource.ResourceGen
import pja.s20131.librarysystem.resource.SeriesDatabaseHelper

@SpringBootTest
class EbookServiceTests @Autowired constructor(
    private val ebookService: EbookService,
    private val ebookDatabaseHelper: EbookDatabaseHelper,
    private val authorDatabaseHelper: AuthorDatabaseHelper,
    private val seriesDatabaseHelper: SeriesDatabaseHelper,
) : BaseTestConfig() {

    @Test
    fun `should get all ebooks`() {
        val ebook1 = EbookGen.ebook(author = DEFAULT_AUTHOR, series = DEFAULT_SERIES)
        val ebook2 = EbookGen.ebook(author = DEFAULT_AUTHOR, series = null)
        authorDatabaseHelper.insertAuthor(DEFAULT_AUTHOR)
        seriesDatabaseHelper.insertSeries(DEFAULT_SERIES)
        ebookDatabaseHelper.insertEbook(ebook1)
        ebookDatabaseHelper.insertEbook(ebook2)

        val response = ebookService.getAllEbooks()

        assertThat(response).containsExactly(
            ResourceWithAuthorBasicData(ebook1.toBasicData(), DEFAULT_AUTHOR.toBasicData()),
            ResourceWithAuthorBasicData(ebook2.toBasicData(), DEFAULT_AUTHOR.toBasicData()),
        )
    }

    // TODO how to compare?
    @Disabled("content comparison")
    @Test
    fun `should return an ebook`() {
        val ebook = EbookGen.ebook(author = DEFAULT_AUTHOR, series = DEFAULT_SERIES)
        authorDatabaseHelper.insertAuthor(DEFAULT_AUTHOR)
        seriesDatabaseHelper.insertSeries(DEFAULT_SERIES)
        ebookDatabaseHelper.insertEbook(ebook)

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
        val command = EbookGen.addEbookCommand(authorId = DEFAULT_AUTHOR.authorId)
        authorDatabaseHelper.insertAuthor(DEFAULT_AUTHOR)
        seriesDatabaseHelper.insertSeries(command.series!!)

        val ebookId = ebookService.addEbook(command)

        ebookDatabaseHelper.assertEbookIsSaved(ebookId)
    }

    @Test
    fun `should throw an exception when adding an ebook with not existing author id`() {
        val command = EbookGen.addEbookCommand()

        assertThrows<AuthorNotFoundException> { ebookService.addEbook(command) }
    }

    companion object {
        private val DEFAULT_AUTHOR = ResourceGen.author()
        private val DEFAULT_SERIES = Series("series")
    }
}
