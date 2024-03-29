package pja.s20131.librarysystem.storage

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.IntegrationTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.domain.resource.StorageService
import pja.s20131.librarysystem.domain.resource.StoredResource
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.user.port.UserNotFoundException
import pja.s20131.librarysystem.resource.ResourceGen
import pja.s20131.librarysystem.user.UserGen

@SpringBootTest
class StorageServiceTests @Autowired constructor(
    private val storageService: StorageService,
    private val given: Preconditions,
    private val assert: Assertions,
) : IntegrationTestConfig() {

    @Test
    fun `should get user's storage`() {
        val (author, books, ebooks) = given.author.exists()
            .withBook(series = ResourceGen.defaultSeries)
            .withEbook(series = ResourceGen.defaultSeries)
            .build()
        val user = given.user.exists(
            itemsInStorage = books.map { it.resourceId to clock.now() } + ebooks.map { it.resourceId to clock.now() }
        ).build()

        val response = storageService.getUserStorage(user.userId)

        assertThat(response).containsExactlyInAnyOrder(
            StoredResource(books[0].toBasicData(), author.toBasicData(), ResourceType.BOOK, clock.now()),
            StoredResource(ebooks[0].toBasicData(), author.toBasicData(), ResourceType.EBOOK, clock.now()),
        )
    }

    // TODO parametrized
    @Test
    fun `should get user's storage ordered by added time`() {
        val (author, books, ebooks) = given.author.exists()
            .withBook(series = ResourceGen.defaultSeries)
            .withEbook(series = ResourceGen.defaultSeries)
            .withEbook()
            .build()
        val user = given.user.exists(
            itemsInStorage = listOf(ebooks[0].resourceId to clock.lastWeek(), books[0].resourceId to clock.now(), ebooks[1].resourceId to clock.yesterday())
        ).build()

        val response = storageService.getUserStorage(user.userId)

        assertThat(response).containsExactly(
            StoredResource(books[0].toBasicData(), author.toBasicData(), ResourceType.BOOK, clock.now()),
            StoredResource(ebooks[1].toBasicData(), author.toBasicData(), ResourceType.EBOOK, clock.yesterday()),
            StoredResource(ebooks[0].toBasicData(), author.toBasicData(), ResourceType.EBOOK, clock.lastWeek()),
        )
    }

    @Test
    fun `should get empty user's storage when nothing was added`() {
        val user = given.user.exists().build()

        val response = storageService.getUserStorage(user.userId)

        assertThat(response).isEqualTo(emptyList<StoredResource>())
    }

    @Test
    fun `should throw exception when getting not existing user's storage`() {
        val user = UserGen.user()

        assertThrows<UserNotFoundException> { storageService.getUserStorage(user.userId) }
    }

    @Test
    fun `should add item to user's storage`() {
        val user = given.user.exists().build()
        val (_, books) = given.author.exists().withBook().build()

        storageService.addToUserStorage(user.userId, books[0].resourceId)

        assert.resource.isSavedInStorage(user.userId, books[0].resourceId, ResourceType.BOOK)
    }

    @Test
    fun `should remove item from user's storage`() {
        val book = given.author.exists().withBook().build().second[0]
        val user = given.user.exists(itemsInStorage = listOf(book.resourceId to clock.instant())).build()

        storageService.removeFromUserStorage(user.userId, book.resourceId)

        assert.resource.isNotSavedInStorage(user.userId, book.resourceId)
    }
}
