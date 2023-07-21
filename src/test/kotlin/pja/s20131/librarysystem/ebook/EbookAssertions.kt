package pja.s20131.librarysystem.ebook

import org.assertj.core.api.Assertions.assertThat
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.ebook.EbookDatabaseHelper

@Component
class EbookAssertions(
    private val ebookDatabaseHelper: EbookDatabaseHelper,
) {
    fun isSaved(ebookId: ResourceId) {
        val ebook = ebookDatabaseHelper.findBy(ebookId)

        assertThat(ebook).isNotNull
        assertThat(ebook!!.resourceId).isEqualTo(ebookId)
    }
}
