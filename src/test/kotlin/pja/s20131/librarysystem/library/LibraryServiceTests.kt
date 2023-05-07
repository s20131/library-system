package pja.s20131.librarysystem.library

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.domain.library.LibraryService
import pja.s20131.librarysystem.preconditions.Preconditions

@SpringBootTest
class LibraryServiceTests @Autowired constructor(
    val libraryService: LibraryService,
    val assuming: Preconditions,
) : BaseTestConfig() {

    @Test
    fun `should get all libraries`() {
        val library1 = assuming.library.exists()
        val library2 = assuming.library.exists()

        val response = libraryService.getAllLibraries()

        assertThat(response).containsOnly(library1, library2)
    }

}
