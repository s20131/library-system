package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.assertions.Assertions
import pja.s20131.librarysystem.domain.resource.ResourceService
import pja.s20131.librarysystem.domain.resource.StoredResource
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.user.port.UserNotFoundException
import pja.s20131.librarysystem.preconditions.Preconditions
import pja.s20131.librarysystem.user.UserGen
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
class ResourceServiceTests @Autowired constructor(
    private val resourceService: ResourceService,
    private val assuming: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should get user's storage`() {
        val (author, books, ebooks) = assuming.author.exists()
            .withBook(series = ResourceGen.defaultSeries)
            .withEbook(series = ResourceGen.defaultSeries)
            .build()
        // TODO otherwise its value changes on CI
        val now = NOW
        val user = assuming.user.exists(
            itemsInStorage = books.map { it.resourceId to now } + ebooks.map { it.resourceId to now }
        )

        val response = resourceService.getUserStorage(user.userId)

        assertThat(response).containsExactlyInAnyOrder(
            StoredResource(books[0].toBasicData(), author.toBasicData(), ResourceType.BOOK, now),
            StoredResource(ebooks[0].toBasicData(), author.toBasicData(), ResourceType.EBOOK, now),
        )
    }

    // TODO parametrized
    @Test
    fun `should get user's storage ordered by added time`() {
        val (author, books, ebooks) = assuming.author.exists()
            .withBook(series = ResourceGen.defaultSeries)
            .withEbook(series = ResourceGen.defaultSeries)
            .withEbook()
            .build()
        // TODO otherwise its value changes on CI
        val now = NOW
        val yesterday = YESTERDAY
        val lastWeek = LAST_WEEK
        val user = assuming.user.exists(
            itemsInStorage = listOf(ebooks[0].resourceId to lastWeek, books[0].resourceId to now, ebooks[1].resourceId to yesterday)
        )

        val response = resourceService.getUserStorage(user.userId)

        assertThat(response).containsExactly(
            StoredResource(books[0].toBasicData(), author.toBasicData(), ResourceType.BOOK, now),
            StoredResource(ebooks[1].toBasicData(), author.toBasicData(), ResourceType.EBOOK, yesterday),
            StoredResource(ebooks[0].toBasicData(), author.toBasicData(), ResourceType.EBOOK, lastWeek),
        )
    }

    @Test
    fun `should get empty user's storage when nothing was added`() {
        val user = assuming.user.exists()

        val response = resourceService.getUserStorage(user.userId)

        assertThat(response).isEqualTo(emptyList<StoredResource>())
    }

    @Test
    fun `should throw exception when getting not existing user's storage`() {
        val user = UserGen.user()

        assertThrows<UserNotFoundException> { resourceService.getUserStorage(user.userId) }
    }

    @Test
    fun `should add item to user's storage`() {
        val user = assuming.user.exists()
        val (_, books) = assuming.author.exists().withBook().build()

        resourceService.addToUserStorage(user.userId, books[0].resourceId)

        assert.resource.isSavedInStorage(user.userId, books[0].resourceId, ResourceType.BOOK)
    }

    @Test
    fun `should remove item from user's storage`() {
        val book = assuming.author.exists().withBook().build().second[0]
        val user = assuming.user.exists(itemsInStorage = listOf(book.resourceId to YESTERDAY))

        resourceService.removeFromUserStorage(user.userId, book.resourceId)

        assert.resource.isNotSavedInStorage(user.userId, book.resourceId)
    }

    companion object {
        // TODO replace with clock injection
        private val NOW = Instant.now()
        private val YESTERDAY = Instant.now().minus(1, ChronoUnit.DAYS)
        private val LAST_WEEK = Instant.now().minus(7, ChronoUnit.DAYS)
    }
}
