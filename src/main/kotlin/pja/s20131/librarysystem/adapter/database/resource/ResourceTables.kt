package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import pja.s20131.librarysystem.adapter.database.shared.TextTable
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.FirstName
import pja.s20131.librarysystem.domain.resource.model.LastName
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus

object AuthorTable : UUIDTable("author") {
    val firstName = text("first_name")
    val lastName = text("last_name")
}

object SeriesTable : TextTable("series", "name")

object ResourceTable : UUIDTable("resource") {
    val title = text("title")
    val author = uuid("author").references(AuthorTable.id)
    val releaseDate = date("release_date")
    val description = text("description").nullable()
    val series = text("series").references(SeriesTable.id).nullable()
    val status = enumerationByName<ResourceStatus>("status", 255)
}

internal fun ResultRow.toAuthor() =
    Author(
        FirstName(this[AuthorTable.firstName]),
        LastName(this[AuthorTable.lastName]),
    )
