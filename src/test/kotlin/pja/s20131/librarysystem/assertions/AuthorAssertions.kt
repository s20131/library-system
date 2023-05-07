package pja.s20131.librarysystem.assertions

import org.assertj.core.api.Assertions.assertThat
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.resource.AuthorDatabaseHelper

@Component
class AuthorAssertions(
    private val authorDatabaseHelper: AuthorDatabaseHelper,
) {
    fun isSaved(authorId: AuthorId) {
        val author = authorDatabaseHelper.findBy(authorId)

        assertThat(author).isNotNull
        assertThat(author!!.authorId).isEqualTo(authorId)
    }
}
