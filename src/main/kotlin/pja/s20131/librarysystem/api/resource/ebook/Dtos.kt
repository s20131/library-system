package pja.s20131.librarysystem.api.resource.ebook

import pja.s20131.librarysystem.domain.resource.ebook.Content
import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.ebook.Ebook
import pja.s20131.librarysystem.domain.resource.ebook.Format
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.ebook.Size
import pja.s20131.librarysystem.domain.resource.ebook.SizeUnit
import pja.s20131.librarysystem.domain.resource.Title

data class GetEbookResponse(
    val title: Title,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val resourceStatus: ResourceStatus,
    val format: Format,
    val content: Content,
    val size: Size,
    val sizeUnit: SizeUnit,
) {

    companion object {
        fun Ebook.toGetEbookResponse() = GetEbookResponse(title, releaseDate, description, series, resourceStatus, format, content, size, sizeUnit)
    }
}
