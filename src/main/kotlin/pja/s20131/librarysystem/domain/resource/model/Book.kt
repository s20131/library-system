package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.domain.resource.AddBookDto

data class Book(
    override val resourceId: ResourceId,
    override val title: Title,
    override val authorId: AuthorId,
    override val releaseDate: ReleaseDate,
    override val description: Description?,
    override val series: Series?,
    override val status: ResourceStatus,
    val isbn: ISBN,
) : Resource() {

    fun toBookBasicData() = BookBasicData(resourceId, title, isbn)

    companion object {
        fun from(
            title: Title,
            authorId: AuthorId,
            releaseDate: ReleaseDate,
            description: Description?,
            series: Series?,
            status: ResourceStatus, isbn: ISBN
        ) = Book(ResourceId.generate(), title, authorId, releaseDate, description, series, status, isbn)

        fun from(dto: AddBookDto) =
            Book(ResourceId.generate(), dto.title, dto.authorId, dto.releaseDate, dto.description, dto.series, dto.status, dto.isbn)
    }
}

@JvmInline
value class ISBN(val value: String)

data class BookBasicData(
    val id: ResourceId,
    val title: Title,
    val isbn: ISBN,
)