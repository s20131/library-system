package pja.s20131.librarysystem.book

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.adapter.database.resource.BookNotFoundException
import pja.s20131.librarysystem.domain.resource.BookService
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.port.AuthorNotFoundException
import pja.s20131.librarysystem.resource.AuthorDatabaseHelper
import pja.s20131.librarysystem.resource.ResourceGen
import pja.s20131.librarysystem.resource.SeriesDatabaseHelper

@SpringBootTest
class BookServiceTests @Autowired constructor(
    private val bookService: BookService,
    private val bookDatabaseHelper: BookDatabaseHelper,
    private val authorDatabaseHelper: AuthorDatabaseHelper,
    private val seriesDatabaseHelper: SeriesDatabaseHelper,
) : BaseTestConfig() {

    @Test
    fun `should return all books`() {
        val book1 = BookGen.book(author = DEFAULT_AUTHOR, series = DEFAULT_SERIES)
        val book2 = BookGen.book(author = DEFAULT_AUTHOR, series = null)
        authorDatabaseHelper.insertAuthor(DEFAULT_AUTHOR)
        seriesDatabaseHelper.insertSeries(DEFAULT_SERIES)
        bookDatabaseHelper.insertBook(book1)
        bookDatabaseHelper.insertBook(book2)

        val response = bookService.getAllBooks()

        assertThat(response).containsExactly(
            ResourceWithAuthorBasicData(book1.toBasicData(), DEFAULT_AUTHOR.toBasicData()),
            ResourceWithAuthorBasicData(book2.toBasicData(), DEFAULT_AUTHOR.toBasicData()),
        )
    }

    @Test
    fun `should return a book`() {
        val book = BookGen.book(author = DEFAULT_AUTHOR)
        authorDatabaseHelper.insertAuthor(DEFAULT_AUTHOR)
        bookDatabaseHelper.insertBook(book)

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
        val command = BookGen.addBookCommand(authorId = DEFAULT_AUTHOR.authorId, series = DEFAULT_SERIES)
        authorDatabaseHelper.insertAuthor(DEFAULT_AUTHOR)
        seriesDatabaseHelper.insertSeries(command.series!!)

        val bookId = bookService.addBook(command)

        bookDatabaseHelper.assertBookIsSaved(bookId)
    }

    @Test
    fun `should throw an exception when adding a book with not existing author id`() {
        val command = BookGen.addBookCommand()

        assertThrows<AuthorNotFoundException> { bookService.addBook(command) }
    }

    companion object {
        private val DEFAULT_AUTHOR = ResourceGen.author()
        private val DEFAULT_SERIES = Series("series")
    }
}
