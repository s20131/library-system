package pja.s20131.librarysystem.library

import org.assertj.core.api.Assertions.assertThat
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.ResourceId

@Component
class LibraryAssertions(
    private val copyDatabaseHelper: CopyDatabaseHelper,
) {
    fun hasCopies(expectedCopies: Int, libraryId: LibraryId, resourceId: ResourceId) {
        val copies = copyDatabaseHelper.getCopiesCount(libraryId, resourceId)
        assertThat(expectedCopies).isEqualTo(copies)
    }
}
