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
    val format: Format,
    val content: Content,
    val size: Size,
    val sizeUnit: SizeUnit
)

@JvmInline
value class Format(val raw: String)

@JvmInline
value class Content(val raw: ByteArray)

@JvmInline
value class Size(val raw: Double)

enum class SizeUnit {
    kB, MB
}
