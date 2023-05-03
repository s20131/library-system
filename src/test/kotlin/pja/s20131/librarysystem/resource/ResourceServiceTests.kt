package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.book.BookDatabaseHelper
import pja.s20131.librarysystem.book.BookGen
import pja.s20131.librarysystem.domain.resource.ResourceService
import pja.s20131.librarysystem.domain.resource.StoredResource
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.user.port.UserNotFoundException
import pja.s20131.librarysystem.ebook.EbookDatabaseHelper
import pja.s20131.librarysystem.ebook.EbookGen
import pja.s20131.librarysystem.user.UserDatabaseHelper
import pja.s20131.librarysystem.user.UserGen
import java.time.Instant

@SpringBootTest
class ResourceServiceTests @Autowired constructor(
    private val resourceService: ResourceService,
    private val bookDatabaseHelper: BookDatabaseHelper,
    private val ebookDatabaseHelper: EbookDatabaseHelper,
    private val authorDatabaseHelper: AuthorDatabaseHelper,
    private val seriesDatabaseHelper: SeriesDatabaseHelper,
    private val userDatabaseHelper: UserDatabaseHelper,
    private val storageDatabaseHelper: StorageDatabaseHelper,
) : BaseTestConfig() {

    @Test
    fun `should get user's storage`() {
        authorDatabaseHelper.insertAuthor(DEFAULT_AUTHOR)
        seriesDatabaseHelper.insertSeries(DEFAULT_SERIES)
        val book = BookGen.book(author = DEFAULT_AUTHOR, series = DEFAULT_SERIES)
        bookDatabaseHelper.insertBook(book)
        val ebook = EbookGen.ebook(author = DEFAULT_AUTHOR, series = DEFAULT_SERIES)
        ebookDatabaseHelper.insertEbook(ebook)
        val since = Instant.now()
        val user = UserGen.user()
        userDatabaseHelper.insertUser(user)
        storageDatabaseHelper.insertToStorage(user.userId, book.resourceId, since)
        storageDatabaseHelper.insertToStorage(user.userId, ebook.resourceId, since)

        val response = resourceService.getUserStorage(user.userId)

        assertThat(response).containsExactly(
            StoredResource(book.toBasicData(), DEFAULT_AUTHOR.toBasicData(), ResourceType.BOOK, since),
            StoredResource(ebook.toBasicData(), DEFAULT_AUTHOR.toBasicData(), ResourceType.EBOOK, since),
        )
    }

    @Test
    fun `should get empty user's storage when nothing was added`() {
        val user = UserGen.user()
        userDatabaseHelper.insertUser(user)

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
        val user = UserGen.user()
        userDatabaseHelper.insertUser(user)
        authorDatabaseHelper.insertAuthor(DEFAULT_AUTHOR)
        val book = BookGen.book(author = DEFAULT_AUTHOR)
        bookDatabaseHelper.insertBook(book)

        resourceService.addToUserStorage(user.userId, book.resourceId)

        storageDatabaseHelper.assertResourceIsSavedInStorage(user.userId, book.resourceId, ResourceType.BOOK)
    }

    companion object {
        private val DEFAULT_AUTHOR = ResourceGen.author()
        private val DEFAULT_SERIES = Series("series")
    }
}
