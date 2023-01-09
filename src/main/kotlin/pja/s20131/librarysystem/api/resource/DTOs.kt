package pja.s20131.librarysystem.api.resource

import pja.s20131.librarysystem.api.resource.ResourceBasicData.Companion.toBasicResourceData
import pja.s20131.librarysystem.domain.resource.Book
import pja.s20131.librarysystem.domain.resource.Content
import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.Ebook
import pja.s20131.librarysystem.domain.resource.Format
import pja.s20131.librarysystem.domain.resource.ISBN
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.Resource
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Size
import pja.s20131.librarysystem.domain.resource.SizeUnit
import pja.s20131.librarysystem.domain.resource.Title

data class GetBookResponse(val resource: ResourceBasicData, val isbn: ISBN) {

    companion object {
        fun Book.toGetBookResponse() = GetBookResponse(resource.toBasicResourceData(), isbn)
    }
}

data class GetEbookResponse(val resource: ResourceBasicData, val format: Format, val content: Content, val size: Size, val sizeUnit: SizeUnit) {

    companion object {
        fun Ebook.toGetEbookResponse() = GetEbookResponse(resource.toBasicResourceData(), format, content, size, sizeUnit)
    }
}

data class ResourceBasicData(
    val title: Title,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val resourceStatus: ResourceStatus
) {

    companion object {
        fun Resource.toBasicResourceData() = ResourceBasicData(this.title, this.releaseDate, this.description, this.series, this.resourceStatus)
    }
}
