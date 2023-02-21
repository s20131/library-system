package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
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

fun insertResourcePropertiesFrom(resource: Resource) {
    ResourceTable.insert {
        it[id] = resource.resourceId.value
        it[title] = resource.title.value
        it[author] = resource.author.authorId.value
        it[releaseDate] = resource.releaseDate.value
        it[description] = resource.description?.value
        it[series] = resource.series?.value
        it[status] = resource.status
    }
}
