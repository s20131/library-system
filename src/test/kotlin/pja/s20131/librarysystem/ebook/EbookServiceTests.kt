package pja.s20131.librarysystem.ebook

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.IntegrationTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.adapter.database.resource.EbookNotFoundException
import pja.s20131.librarysystem.domain.resource.EbookService
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.RentalCannotBeDownloadedException
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.SearchQuery
import pja.s20131.librarysystem.domain.resource.port.AuthorNotFoundException

@SpringBootTest
class EbookServiceTests @Autowired constructor(
    private val ebookService: EbookService,
    private val given: Preconditions,
    private val assert: Assertions,
) : IntegrationTestConfig() {

    @Test
    fun `should get all ebooks in status available`() {
        val (author, _, ebooks) = given.author.exists().withEbook().withEbook().build()

        val response = ebookService.getAllActiveEbooks()

        val expected = listOf(
            ResourceWithAuthorBasicData(ebooks[0].toBasicData(), author.toBasicData()),
            ResourceWithAuthorBasicData(ebooks[1].toBasicData(), author.toBasicData()),
        )
        assertThat(response).containsAll(expected)
    }

    @Test
    fun `should get all ebooks in status available sorted by title`() {
        val (author, _, ebooks) = given.author.exists().withEbook().withEbook().withEbook(status = ResourceStatus.WAITING_FOR_APPROVAL).build()

        val response = ebookService.getAllActiveEbooks()

        val expected = listOf(
            ResourceWithAuthorBasicData(ebooks[0].toBasicData(), author.toBasicData()),
            ResourceWithAuthorBasicData(ebooks[1].toBasicData(), author.toBasicData()),
        ).sortedBy { it.resource.title.value }
        assertThat(response).isEqualTo(expected)
    }

    @Test
    fun `assert only ebooks in status available are returned`() {
        val (author, _, ebooks) = given.author.exists()
            .withEbook()
            .withEbook(status = ResourceStatus.WAITING_FOR_APPROVAL)
            .withEbook(status = ResourceStatus.WITHDRAWN)
            .withEbook()
            .build()

        val response = ebookService.getAllActiveEbooks()

        val expected = listOf(
            ResourceWithAuthorBasicData(ebooks[0].toBasicData(), author.toBasicData()),
            ResourceWithAuthorBasicData(ebooks[3].toBasicData(), author.toBasicData()),
        )
        assertThat(response).containsAll(expected)
    }

    @Test
    fun `should return an ebook`() {
        val ebook = given.author.exists().withEbook().build().third[0]

        val response = ebookService.getEbook(ebook.resourceId)

        assertThat(ebook.content.bytes).isEqualTo(response.content.bytes)
        assertThat(ebook.copy(content = response.content)).isEqualTo(response)
    }

    @Test
    fun `should throw an exception when getting an ebook which doesn't exist`() {
        val ebook = EbookGen.ebook()

        assertThrows<EbookNotFoundException> { ebookService.getEbook(ebook.resourceId) }
    }

    @Test
    fun `should correctly add an ebook`() {
        val (author) = given.author.exists().build()
        val series = given.series.exists()
        val dto = EbookGen.addEbookDto(authorId = author.authorId, series = series)

        val ebookId = ebookService.addEbook(dto)

        assert.ebook.isSaved(ebookId)
    }

    @Test
    fun `should throw an exception when adding an ebook with not existing author id`() {
        val dto = EbookGen.addEbookDto()

        assertThrows<AuthorNotFoundException> { ebookService.addEbook(dto) }
    }

    @Test
    fun `should find specific ebooks`() {
        val (author, _, ebooks) = given.author.exists()
            .withEbook(description = Description("fajny ebook"))
            .withEbook(description = Description("wyborny ebook"))
            .withEbook(description = Description("fantastyczny ebook"))
            .build()

        val response = ebookService.search(SearchQuery("wyborny i fajny"))

        val expected = listOf(
            ResourceWithAuthorBasicData(ebooks[0].toBasicData(), author.toBasicData()),
            ResourceWithAuthorBasicData(ebooks[1].toBasicData(), author.toBasicData()),
        ).sortedBy { it.resource.title.value }
        assertThat(response).isEqualTo(expected)
    }

    @Test
    fun `should get ebook content`() {
        val ebook = given.author.exists().withEbook().build().third[0]
        val user = given.user.exists().build()
        val library = given.library.exists().hasCopy(ebook.resourceId).build()
        given.rental.exists(user.userId, ebook.resourceId, library.libraryId, RentalPeriod.startRental(clock.now()))

        val response = ebookService.getEbookContent(ebook.resourceId, user.userId)

        assertThat(response.bytes).isEqualTo(ebook.content.bytes)
        assertThat(response.format).isEqualTo(ebook.content.format)
    }

    @Test
    fun `should throw an error when trying to download ebook and having inactive rental`() {
        val ebook = given.author.exists().withEbook().build().third[0]
        val user = given.user.exists().build()
        val library = given.library.exists().hasCopy(ebook.resourceId).build()
        given.rental.exists(user.userId, ebook.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()), RentalStatus.CANCELLED)

        assertThrows<RentalCannotBeDownloadedException> { ebookService.getEbookContent(ebook.resourceId, user.userId) }
    }
}
