package pja.s20131.librarysystem.library

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.domain.library.model.Postcode
import pja.s20131.librarysystem.domain.library.port.LibraryRepository
import pja.s20131.librarysystem.library.LibraryGen.library
import java.sql.SQLException

@SpringBootTest
@Transactional
class LibraryRepositoryTests @Autowired constructor(
    val libraryRepository: LibraryRepository,
    val libraryDatabaseHelper: LibraryDatabaseHelper,
) {

    @BeforeEach
    fun clear() {
        LibraryTable.deleteAll()
    }

    @Test
    fun `should get all libraries`() {
        val library1 = library()
        val library2 = library()

        libraryDatabaseHelper.insertLibrary(library1)
        libraryDatabaseHelper.insertLibrary(library2)

        val response = libraryRepository.getAll()
        assertThat(response).containsOnly(
            library1, library2
        )
    }

    // TODO test real implementation
    @ParameterizedTest
    @ValueSource(strings = ["12345", "abc", "0-5555", "123-45"])
    fun `should return SQL error on inserting invalid postcode`(postcode: String) {
        val library = library(postcode = Postcode(postcode))

        assertThrows<SQLException> { libraryDatabaseHelper.insertLibrary(library) }
    }

}
