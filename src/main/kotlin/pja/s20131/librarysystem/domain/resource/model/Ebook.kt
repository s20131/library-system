package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.domain.resource.AddEbookCommand
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

        fun from(command: AddEbookCommand) = Ebook(
            ResourceId.generate(),
            command.title,
            command.authorId,
            command.releaseDate,
            command.description,
            command.series,
            command.status,
            command.content,
            command.format,
            command.size
        )
    }
}

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
}

enum class SizeUnit {
    kB
}

class NegativeSizeException(value: Double) : BaseException("Size cannot be negative, but was $value")
