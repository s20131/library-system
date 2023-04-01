package pja.s20131.librarysystem.book

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.AuthorTable
import pja.s20131.librarysystem.adapter.database.resource.BookTable
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable
import pja.s20131.librarysystem.book.BookGen.book
import pja.s20131.librarysystem.domain.resource.port.BookRepository
import pja.s20131.librarysystem.resource.ResourceDatabaseHelper

@SpringBootTest
@Transactional
class BookRepositoryTests @Autowired constructor(
    val bookRepository: BookRepository,
    val bookDatabaseHelper: BookDatabaseHelper,
    val resourceDatabaseHelper: ResourceDatabaseHelper,
) {

    @AfterEach
    fun clear() {
        BookTable.deleteAll()
        ResourceTable.deleteAll()
        AuthorTable.deleteAll()
        SeriesTable.deleteAll()
    }

    @Test
    fun `should return all books`() {
        val (book1, book2) = book() to book()
        bookDatabaseHelper.insertBook(book1)
        bookDatabaseHelper.insertBook(book2)

        val response = bookRepository.getAll()

        assertThat(response).containsExactly(book1, book2)
    }

    @Test
    fun `should correctly add a book`() {
        val book = book()
        resourceDatabaseHelper.insertResourceDependencies(book.author, book.series!!)

        bookRepository.save(book)

        val response = bookDatabaseHelper.getAllBooks()
        assertThat(response).containsExactly(book)
    }

}
