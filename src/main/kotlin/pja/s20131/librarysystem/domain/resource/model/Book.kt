package pja.s20131.librarysystem.domain.resource.model

import pja.s20131.librarysystem.domain.resource.AddBookCommand

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

    companion object {
        fun from(
            title: Title,
            authorId: AuthorId,
            releaseDate: ReleaseDate,
            description: Description?,
            series: Series?,
            status: ResourceStatus, isbn: ISBN
        ) = Book(ResourceId.generate(), title, authorId, releaseDate, description, series, status, isbn)

        fun from(command: AddBookCommand) =
            Book(ResourceId.generate(), command.title, command.authorId, command.releaseDate, command.description, command.series, command.status, command.isbn)
    }
}

@JvmInline
value class ISBN(val value: String)
