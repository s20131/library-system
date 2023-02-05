package pja.s20131.librarysystem.api.resource.ebook

import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Title
import pja.s20131.librarysystem.domain.resource.ebook.Content
import pja.s20131.librarysystem.domain.resource.ebook.Ebook
import pja.s20131.librarysystem.domain.resource.ebook.Size

data class GetEbookResponse(
    val title: Title,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val status: ResourceStatus,
    val content: Content,
    val size: Size,
) {

    companion object {
        fun Ebook.toGetEbookResponse() = GetEbookResponse(title, releaseDate, description, series, status, content, size)
    }
}
