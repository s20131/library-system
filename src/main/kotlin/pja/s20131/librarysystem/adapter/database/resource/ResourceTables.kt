package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.statements.InsertStatement
import pja.s20131.librarysystem.domain.resource.model.Resource
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus

object ResourceTable : UUIDTable("resource") {
    val title = text("title")
    val author = uuid("author").references(AuthorTable.id)
    val releaseDate = date("release_date")
    val description = text("description").nullable()
    val series = text("series").references(SeriesTable.id).nullable()
    val status = enumerationByName<ResourceStatus>("status", 255)
}

fun InsertStatement<Number>.from(resource: Resource) {
    this[ResourceTable.id] = resource.resourceId.value
    this[ResourceTable.title] = resource.title.value
    this[ResourceTable.author] = resource.authorId.value
    this[ResourceTable.releaseDate] = resource.releaseDate.value
    this[ResourceTable.description] = resource.description?.value
    this[ResourceTable.series] = resource.series?.value
    this[ResourceTable.status] = resource.status
}
