package pja.s20131.librarysystem.adapter.api.resource.ebook

import com.fasterxml.jackson.annotation.JsonCreator
import pja.s20131.librarysystem.domain.resource.AddEbookDto
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Format
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Size
import pja.s20131.librarysystem.domain.resource.model.Title
import java.time.LocalDate
import java.util.UUID

data class AddEbookRequest(
    val title: Title,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val authorId: AuthorId,
) {
    fun toDto(format: Format, content: ByteArray) = AddEbookDto(title, authorId, releaseDate, description, series, content, format)

    companion object {
        @JvmStatic
        @JsonCreator
        fun creator(title: String, releaseDate: LocalDate, description: String?, series: String?, authorId: UUID) =
            AddEbookRequest(
                Title(title),
                ReleaseDate(releaseDate),
                description?.let { Description(it) },
                series?.let { Series(it) },
                AuthorId(authorId),
            )
    }
}

data class GetEbookInfoResponse(
    val title: Title,
    val authorId: AuthorId,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val status: ResourceStatus,
    val format: Format,
    val size: Size,
)
