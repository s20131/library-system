package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.domain.resource.AddEbookDto
import pja.s20131.librarysystem.exception.BaseException

data class Ebook(
    override val resourceId: ResourceId,
    override val title: Title,
    override val authorId: AuthorId,
    override val releaseDate: ReleaseDate,
    override val description: Description?,
    override val series: Series?,
    override val status: ResourceStatus,
    // TODO could be stored separately - many formats to choose
    val content: EbookContent,
    val size: Size,
) : Resource() {

    companion object {
        fun from(
            title: Title,
            authorId: AuthorId,
            releaseDate: ReleaseDate,
            description: Description?,
            series: Series?,
            status: ResourceStatus,
            content: EbookContent,
            size: Size,
        ) = Ebook(ResourceId.generate(), title, authorId, releaseDate, description, series, status, content, size)

        fun from(dto: AddEbookDto) = Ebook(
            ResourceId.generate(),
            dto.title,
            dto.authorId,
            dto.releaseDate,
            dto.description,
            dto.series,
            dto.status,
            EbookContent(dto.content, dto.format),
            dto.size
        )
    }
}

@Suppress("ArrayInDataClass")
data class EbookContent(val bytes: ByteArray, val format: Format)

enum class Format {
    PDF, EPUB
}

@JvmInline
value class Size(val value: Int) {
    init {
        if (value < 0) throw NegativeSizeException(value)
    }
}

class NegativeSizeException(value: Int) : BaseException("Size cannot be negative, but was $value")
