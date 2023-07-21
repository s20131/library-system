package pja.s20131.librarysystem.adapter.api.resource.book

import com.fasterxml.jackson.annotation.JsonCreator
import pja.s20131.librarysystem.domain.resource.AddBookDto
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Title
import java.time.LocalDate
import java.util.UUID

data class AddBookRequest(
    val title: Title,
    val authorId: AuthorId,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val isbn: ISBN,
) {
    fun toDto() = AddBookDto(title, authorId, releaseDate, description, series, isbn)

    companion object {
        @JvmStatic
        @JsonCreator
        fun creator(title: String, authorId: UUID, releaseDate: LocalDate, description: String?, series: String?, isbn: String) =
            AddBookRequest(
                Title(title),
                AuthorId(authorId),
                ReleaseDate(releaseDate),
                description?.let { Description(it) },
                series?.let { Series(it) },
                ISBN(isbn)
            )
    }
}

data class GetBookResponse(
    val title: Title,
    val authorId: AuthorId,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val status: ResourceStatus,
    val isbn: ISBN,
)
