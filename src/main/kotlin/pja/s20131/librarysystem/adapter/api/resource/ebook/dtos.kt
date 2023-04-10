package pja.s20131.librarysystem.adapter.api.resource.ebook

import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Content
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Format
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Size
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.port.AddEbookCommand

data class AddEbookRequest(
    val title: Title,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val status: ResourceStatus,
    val content: Content,
    val format: Format,
    val size: Size,
    val authorId: AuthorId,
) {
    fun toCommand() = AddEbookCommand(title, authorId, releaseDate, description, series, status, content, format, size)
}

data class GetEbookResponse(
    val title: Title,
    val authorId: AuthorId,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val status: ResourceStatus,
    val content: Content,
    val format: Format,
    val size: Size,
)
