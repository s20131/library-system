package pja.s20131.librarysystem.library

import java.sql.SQLException
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.adapter.database.library.LibraryRepository
import pja.s20131.librarysystem.domain.library.Postcode

@SpringBootTest
class LibraryRepositoryTests @Autowired constructor(
    val libraryRepository: LibraryRepository
) {

    @BeforeEach
    fun clear() {
        transaction {
            LibraryTable.deleteAll()
        }
    }

    @Test
    fun `should get all libraries`() {
        val library1 = library()
        val library2 = library()

        addLibrary(library1)
        addLibrary(library2)

        val response = transaction { libraryRepository.getAll() }
        assertThat(response).containsOnly(
            library1, library2
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["12345", "abc", "0-5555", "123-45"])
    fun `should return SQL error on inserting invalid postcode`(postcode: String) {
        val library = library(postcode = Postcode(postcode))

        assertThrows<SQLException> { addLibrary(library) }
    }

}
