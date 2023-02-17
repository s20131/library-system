package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import pja.s20131.librarysystem.adapter.database.exposed.TextIdTable
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus

object AuthorTable : UUIDTable("author") {
    val firstName = text("first_name")
    val lastName = text("last_name")
}

object SeriesTable : TextIdTable("series", "name")

object GenreTable : TextIdTable("genre", "name")

object ResourceTable : UUIDTable("resource") {
    val title = text("title")
    val author = uuid("author").references(AuthorTable.id)
    val releaseDate = date("release_date")
    val description = text("description").nullable()
    val series = text("series").references(SeriesTable.id).nullable()
    val status = enumerationByName<ResourceStatus>("status", 255)
}

object ResourceGenresTable : Table("resource_genres") {
    val resource = reference("resource", ResourceTable)
    val genre = reference("genre", GenreTable)
    override val primaryKey = PrimaryKey(resource, genre)
}

internal fun ResultRow.toAuthor() =
    Author(
        FirstName(this[AuthorTable.firstName]),
        LastName(this[AuthorTable.lastName]),
    )
