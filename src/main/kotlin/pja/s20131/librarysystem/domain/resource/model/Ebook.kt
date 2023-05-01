package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.exception.BaseException

data class Ebook(
    override val resourceId: ResourceId,
    override val title: Title,
    override val authorId: AuthorId,
    override val releaseDate: ReleaseDate,
    override val description: Description?,
    override val series: Series?,
    override val status: ResourceStatus,
    val content: Content,
    val format: Format,
    val size: Size,
) : Resource()

@JvmInline
value class Content(val value: ByteArray)

enum class Format {
    PDF, MOBI, EPUB
}

@JvmInline
value class Size(val value: Double) {
    init {
        if (value < 0) throw NegativeSizeException(value)
    }

    class NegativeSizeException(value: Double) : BaseException("Size cannot be negative - given size=$value")
}

enum class SizeUnit {
    kB
}
