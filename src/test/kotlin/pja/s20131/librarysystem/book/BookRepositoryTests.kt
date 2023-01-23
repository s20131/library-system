package pja.s20131.librarysystem.book

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.adapter.database.BookTable
import pja.s20131.librarysystem.adapter.database.shared.ResourceTable
import pja.s20131.librarysystem.adapter.database.shared.SeriesTable
import pja.s20131.librarysystem.domain.resource.book.BookRepository

@SpringBootTest
class BookRepositoryTests @Autowired constructor(
    val bookRepository: BookRepository
) {

    @AfterEach
    fun clear() {
        transaction {
            BookTable.deleteAll()
            ResourceTable.deleteAll()
            SeriesTable.deleteAll()
        }
    }

    @Test
    fun `should return all books`() {
        val book1 = book()
        val book2 = book()

        addBook(book1)
        addBook(book2)

        val response = transaction { bookRepository.getAll() }
        assertThat(response).containsExactly(
            book1, book2
        )
    }

}