package pja.s20131.librarysystem.domain.resource.ebook

import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.ResourceId
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Title

data class Ebook(
    val resourceId: ResourceId,
    val title: Title,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val resourceStatus: ResourceStatus,
    val content: Content,
    //val contentType: ContentType,
    val size: Size,
)


@JvmInline
value class Content(val raw: ByteArray)

/*enum class ContentType {
    PDF, MOBI, EPUB
}*/

@JvmInline
value class Size(val raw: Double)
