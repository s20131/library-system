package pja.s20131.librarysystem.book

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.IntegrationTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.adapter.database.resource.BookNotFoundException
import pja.s20131.librarysystem.domain.resource.BookService
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.SearchQuery
import pja.s20131.librarysystem.domain.resource.port.AuthorNotFoundException

@SpringBootTest
class BookServiceTests @Autowired constructor(
    private val bookService: BookService,
    private val given: Preconditions,
    private val assert: Assertions,
) : IntegrationTestConfig() {

    @Test
    fun `should return all books in status available`() {
        val (author, books) = given.author.exists().withBook().withBook().build()

        val response = bookService.getAllActiveBooks()

        val expected = listOf(
            ResourceWithAuthorBasicData(books[0].toBasicData(), author.toBasicData()),
            ResourceWithAuthorBasicData(books[1].toBasicData(), author.toBasicData()),
        )
        assertThat(response).containsAll(expected)
    }

    @Test
    fun `should return all books in status available sorted by title`() {
        val (author, books) = given.author.exists().withBook(status = ResourceStatus.WITHDRAWN).withBook().withBook().build()

        val response = bookService.getAllActiveBooks()

        val expected = listOf(
            ResourceWithAuthorBasicData(books[1].toBasicData(), author.toBasicData()),
            ResourceWithAuthorBasicData(books[2].toBasicData(), author.toBasicData()),
        ).sortedBy { it.resource.title.value }
        assertThat(response).isEqualTo(expected)
    }

    @Test
    fun `assert only books in status available are returned`() {
        val (author, books) = given.author.exists()
            .withBook()
            .withBook(status = ResourceStatus.WAITING_FOR_APPROVAL)
            .withBook(status = ResourceStatus.WITHDRAWN)
            .withBook()
            .build()

        val response = bookService.getAllActiveBooks()

        val expected = listOf(
            ResourceWithAuthorBasicData(books[0].toBasicData(), author.toBasicData()),
            ResourceWithAuthorBasicData(books[3].toBasicData(), author.toBasicData()),
        )
        assertThat(response).containsAll(expected)
    }

    @Test
    fun `should return a book`() {
        val book = given.author.exists().withBook().build().second[0]

        val response = bookService.getActiveBook(book.resourceId)

        assertThat(book).isEqualTo(response)
    }

    @Test
    fun `assert only book in status available is returned`() {
        val book = given.author.exists().withBook(status = ResourceStatus.WITHDRAWN).build().second[0]

        assertThrows<BookNotFoundException> { bookService.getActiveBook(book.resourceId) }
    }

    @Test
    fun `should throw an exception when getting a book which doesn't exist`() {
        val book = BookGen.book()

        assertThrows<BookNotFoundException> { bookService.getActiveBook(book.resourceId) }
    }

    @Test
    fun `should get a book short info by isbn`() {
        val (author, books) = given.author.exists().withBook().build()

        val response = bookService.getBook(books[0].isbn)

        assertThat(response).isEqualTo(ResourceWithAuthorBasicData(books[0].toBasicData(), author.toBasicData()))
    }

    @Test
    fun `assert even book in not available status is returned`() {
        val book = given.author.exists().withBook(status = ResourceStatus.WITHDRAWN).build().second[0]

        assertDoesNotThrow { bookService.getBook(book.isbn) }
    }

    @Test
    fun `should correctly add a book`() {
        val (author) = given.author.exists().build()
        val dto = BookGen.addBookDto(authorId = author.authorId)

        val bookId = bookService.addBook(dto)

        assert.book.isSaved(bookId)
    }

    @Test
    fun `should throw an exception when adding a book with not existing author id`() {
        val dto = BookGen.addBookDto()

        assertThrows<AuthorNotFoundException> { bookService.addBook(dto) }
    }

    @Test
    fun `should find specific book`() {
        val (author, books) = given.author.exists()
            .withBook(description = Description("majestic book"))
            .withBook(description = Description("some book"))
            .withBook(description = Description("another book"))
            .build()

        val response = bookService.search(SearchQuery("majestic"))

        assertThat(response).containsExactly(
            ResourceWithAuthorBasicData(books[0].toBasicData(), author.toBasicData())
        )
    }
}
