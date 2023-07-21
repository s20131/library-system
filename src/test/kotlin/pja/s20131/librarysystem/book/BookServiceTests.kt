package pja.s20131.librarysystem.book

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.adapter.database.resource.BookNotFoundException
import pja.s20131.librarysystem.domain.resource.BookService
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.SearchQuery
import pja.s20131.librarysystem.domain.resource.port.AuthorNotFoundException
import pja.s20131.librarysystem.resource.ResourceGen

@SpringBootTest
class BookServiceTests @Autowired constructor(
    private val bookService: BookService,
    private val bookDatabaseHelper: BookDatabaseHelper,
    private val given: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should return all books in status available`() {
        val (author, books) = given.author.exists().withBook(series = ResourceGen.defaultSeries).withBook().build()

        val response = bookService.getAllActiveBooks()

        val expected = listOf(
            ResourceWithAuthorBasicData(books[0].toBasicData(), author.toBasicData()),
            ResourceWithAuthorBasicData(books[1].toBasicData(), author.toBasicData()),
        )
        assertThat(response).containsAll(expected)
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

        val response = bookService.getBook(book.resourceId)

        assertThat(book).isEqualTo(response)
    }

    @Test
    fun `should throw an exception when getting a book which doesn't exist`() {
        val book = BookGen.book()

        assertThrows<BookNotFoundException> { bookService.getBook(book.resourceId) }
    }

    @Test
    fun `should get a book short info by isbn`() {
        val (author, books) = given.author.exists().withBook().build()

        val response = bookService.getBook(books[0].isbn)

        assertThat(response).isEqualTo(ResourceWithAuthorBasicData(books[0].toBasicData(), author.toBasicData()))
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
        bookDatabaseHelper.refreshSearchView()

        val response = bookService.search(SearchQuery("majestic"))

        assertThat(response).containsExactly(
            ResourceWithAuthorBasicData(books[0].toBasicData(), author.toBasicData())
        )
    }
}
