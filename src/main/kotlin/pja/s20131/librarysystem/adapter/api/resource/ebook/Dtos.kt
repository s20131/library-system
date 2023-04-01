package pja.s20131.librarysystem.adapter.api.resource.ebook

import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Content
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.EbookFormat
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
    val ebookFormat: EbookFormat,
    val size: Size,
    val authorId: AuthorId,
) {
    fun toCommand() = AddEbookCommand(title, authorId, releaseDate, description, series, status, content, ebookFormat, size)
}
