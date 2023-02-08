package pja.s20131.librarysystem.sql

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import pja.s20131.librarysystem.domain.resource.port.BookRepository
import pja.s20131.librarysystem.domain.library.port.LibraryRepository

@SpringBootTest
@Sql(scripts = ["/sql/schema-tmp.sql", "/sql/data.sql"])
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
// TODO doesn't work because test schema isn't dumped
@Disabled("needs better/different configuration")
class SqlTests {

    @Autowired
    lateinit var bookRepository: BookRepository

    @Autowired
    lateinit var libraryRepository: LibraryRepository

    @Test
    fun `assert there is 1 book added`() {
        assertEquals(CURRENT_NUMBER_OF_BOOKS, bookRepository.getAll().size)
    }

    @Test
    fun `assert there are 3 libraries added`() {
        assertEquals(CURRENT_NUMBER_OF_LIBRARIES, libraryRepository.getAll().size)
    }

    companion object {
        const val CURRENT_NUMBER_OF_BOOKS = 1
        const val CURRENT_NUMBER_OF_LIBRARIES = 3
    }
}
