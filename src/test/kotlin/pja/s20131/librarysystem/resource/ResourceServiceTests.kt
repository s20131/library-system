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
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.user.port.UserNotFoundException
import pja.s20131.librarysystem.preconditions.Preconditions
import pja.s20131.librarysystem.user.UserGen
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
class ResourceServiceTests @Autowired constructor(
    private val resourceService: ResourceService,
    private val preconditions: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should get user's storage`() {
        val (author, books, ebooks) = preconditions.resource.authorExists()
            .withBook(series = DEFAULT_SERIES)
            .withEbook(series = DEFAULT_SERIES)
            .build()
        val user = preconditions.user.exists(
            itemsInStorage = books.map { it.resourceId to NOW } + ebooks.map { it.resourceId to NOW }
        )

        val response = resourceService.getUserStorage(user.userId)

        assertThat(response).containsExactlyInAnyOrder(
            StoredResource(books[0].toBasicData(), author.toBasicData(), ResourceType.BOOK, NOW),
            StoredResource(ebooks[0].toBasicData(), author.toBasicData(), ResourceType.EBOOK, NOW),
        )
    }

    // TODO parametrized
    @Test
    fun `should get user's storage ordered by added time`() {
        val (author, books, ebooks) = preconditions.resource.authorExists()
            .withBook(series = DEFAULT_SERIES)
            .withEbook(series = DEFAULT_SERIES)
            .withEbook()
            .build()
        val user = preconditions.user.exists(
            itemsInStorage = listOf(ebooks[0].resourceId to LAST_WEEK, books[0].resourceId to NOW, ebooks[1].resourceId to YESTERDAY)
        )

        val response = resourceService.getUserStorage(user.userId)

        assertThat(response).containsExactly(
            StoredResource(books[0].toBasicData(), author.toBasicData(), ResourceType.BOOK, NOW),
            StoredResource(ebooks[1].toBasicData(), author.toBasicData(), ResourceType.EBOOK, YESTERDAY),
            StoredResource(ebooks[0].toBasicData(), author.toBasicData(), ResourceType.EBOOK, LAST_WEEK),
        )
    }

    @Test
    fun `should get empty user's storage when nothing was added`() {
        val user = preconditions.user.exists()

        val response = resourceService.getUserStorage(user.userId)

        assertThat(response).isEqualTo(emptyList<StoredResource>())
    }

    @Test
    fun `should throw exception when getting not existing user's storage`() {
        val user = UserGen.user()

        assertThrows<UserNotFoundException> { resourceService.getUserStorage(user.userId) }
    }

    @Test
    fun `should add resource to user's storage`() {
        val user = preconditions.user.exists()
        val (_, books) = preconditions.resource.authorExists().withBook().build()

        resourceService.addToUserStorage(user.userId, books[0].resourceId)

        assert.resource.isSavedInStorage(user.userId, books[0].resourceId, ResourceType.BOOK)
    }

    companion object {
        private val NOW = Instant.now()
        private val YESTERDAY = Instant.now().minus(1, ChronoUnit.DAYS)
        private val LAST_WEEK = Instant.now().minus(7, ChronoUnit.DAYS)
        private val DEFAULT_SERIES = Series("series")
    }
}
