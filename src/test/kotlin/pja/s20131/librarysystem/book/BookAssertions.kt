package pja.s20131.librarysystem.book

import org.assertj.core.api.Assertions.assertThat
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.resource.model.ResourceCover
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.resource.ResourceDatabaseHelper

@Component
class BookAssertions(
    private val bookDatabaseHelper: BookDatabaseHelper,
    private val resourceDatabaseHelper: ResourceDatabaseHelper,
) {
    fun isSaved(bookId: ResourceId) {
        val book = bookDatabaseHelper.findBy(bookId)

        assertThat(book).isNotNull
        assertThat(book!!.resourceId).isEqualTo(bookId)
    }

    fun hasCover(bookId: ResourceId, expected: ResourceCover) {
        val cover = resourceDatabaseHelper.getCover(bookId)

        assertThat(cover.content).isEqualTo(expected.content)
        assertThat(cover.mediaType).isEqualTo(expected.mediaType)
    }
}
