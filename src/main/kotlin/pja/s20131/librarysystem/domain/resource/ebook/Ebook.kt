package pja.s20131.librarysystem.domain.resource.ebook

import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.Resource
import pja.s20131.librarysystem.domain.resource.ResourceId
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Title

data class Ebook(
    override val resourceId: ResourceId,
    override val title: Title,
    override val releaseDate: ReleaseDate,
    override val description: Description?,
    override val series: Series?,
    override val status: ResourceStatus,
    val content: Content,
    val ebookFormat: EbookFormat,
    val size: Size,
) : Resource()


@JvmInline
value class Content(val value: ByteArray)

enum class EbookFormat {
    PDF, MOBI, EPUB
}

@JvmInline
value class Size(val value: Double)

enum class SizeUnit {
    kB
}
