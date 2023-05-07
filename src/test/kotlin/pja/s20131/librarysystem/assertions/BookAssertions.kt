package pja.s20131.librarysystem.assertions

import org.assertj.core.api.Assertions.assertThat
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.book.BookDatabaseHelper
import pja.s20131.librarysystem.domain.resource.model.ResourceId

@Component
class BookAssertions(
    private val bookDatabaseHelper: BookDatabaseHelper,
) {
    fun isSaved(bookId: ResourceId) {
        val book = bookDatabaseHelper.findBy(bookId)

        assertThat(book).isNotNull
        assertThat(book!!.resourceId).isEqualTo(bookId)
    }
}
