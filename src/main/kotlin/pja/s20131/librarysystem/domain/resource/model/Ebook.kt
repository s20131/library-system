package pja.s20131.librarysystem.domain.resource.model

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
