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
    val content: Content,
    val format: Format,
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
            content: Content,
            format: Format,
            size: Size,
        ) = Ebook(ResourceId.generate(), title, authorId, releaseDate, description, series, status, content, format, size)

        fun from(dto: AddEbookDto) = Ebook(
            ResourceId.generate(),
            dto.title,
            dto.authorId,
            dto.releaseDate,
            dto.description,
            dto.series,
            dto.status,
            dto.content,
            dto.format,
            dto.size
        )
    }
}

@JvmInline
value class Content(val value: ByteArray)

enum class Format {
    PDF, MOBI, EPUB
}

@JvmInline
value class Size(val value: Int) {
    init {
        if (value < 0) throw NegativeSizeException(value)
    }
}

class NegativeSizeException(value: Int) : BaseException("Size cannot be negative, but was $value")
