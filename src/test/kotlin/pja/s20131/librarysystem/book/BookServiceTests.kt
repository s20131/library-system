package pja.s20131.librarysystem.book

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.adapter.database.resource.BookNotFoundException
import pja.s20131.librarysystem.assertions.Assertions
import pja.s20131.librarysystem.domain.resource.BookService
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.port.AuthorNotFoundException
import pja.s20131.librarysystem.preconditions.Preconditions

@SpringBootTest
class BookServiceTests @Autowired constructor(
    private val bookService: BookService,
    private val preconditions: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should return all books`() {
        val (author, books) = preconditions.resource.authorExists().withBook(series = DEFAULT_SERIES).withBook().build()

        val response = bookService.getAllBooks()

        assertThat(response).containsExactly(
            ResourceWithAuthorBasicData(books[0].toBasicData(), author.toBasicData()),
            ResourceWithAuthorBasicData(books[1].toBasicData(), author.toBasicData()),
        )
    }

    @Test
    fun `should return a book`() {
        val book = preconditions.resource.authorExists().withBook().build().second[0]

        val response = bookService.getBook(book.resourceId)

        assertThat(book).isEqualTo(response)
    }

    @Test
    fun `should throw an exception when getting a book which doesn't exist`() {
        val book = BookGen.book()

        assertThrows<BookNotFoundException> { bookService.getBook(book.resourceId) }
    }

    @Test
    fun `should correctly add a book`() {
        val (author) = preconditions.resource.authorExists().build()
        val command = BookGen.addBookCommand(authorId = author.authorId)

        val bookId = bookService.addBook(command)

        assert.book.isSaved(bookId)
    }

    @Test
    fun `should throw an exception when adding a book with not existing author id`() {
        val command = BookGen.addBookCommand()

        assertThrows<AuthorNotFoundException> { bookService.addBook(command) }
    }

    companion object {
        private val DEFAULT_SERIES = Series("series")
    }
}
