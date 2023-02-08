package pja.s20131.librarysystem.book

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.adapter.database.resource.AuthorTable
import pja.s20131.librarysystem.adapter.database.resource.BookTable
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable
import pja.s20131.librarysystem.domain.resource.port.BookRepository

@SpringBootTest
class BookRepositoryTests @Autowired constructor(
    val bookRepository: BookRepository
) {

    @AfterEach
    fun clear() {
        transaction {
            BookTable.deleteAll()
            ResourceTable.deleteAll()
            AuthorTable.deleteAll()
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
