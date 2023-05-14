package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.resource.BookTable.toBook
import pja.s20131.librarysystem.adapter.database.resource.EbookTable.toEbook
import pja.s20131.librarysystem.domain.resource.model.Resource
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.port.ResourceRepository
import pja.s20131.librarysystem.exception.BaseException

@Repository
class SqlResourceRepository : ResourceRepository {

    override fun getResource(resourceId: ResourceId): Resource {
        return (ResourceTable leftJoin BookTable leftJoin EbookTable)
            .select { ResourceTable.id eq resourceId.value }
            .singleOrNull()
            .let {
                try {
                    it?.toBook()
                } catch (e: Exception) {
                    it?.toEbook()
                }
            } ?: throw ResourceNotFoundException(resourceId)
    }
}

object ResourceTable : UUIDTable("resource") {
    val title = text("title")
    val author = uuid("author").references(AuthorTable.id)
    val releaseDate = date("release_date")
    val description = text("description").nullable()
    val series = text("series").references(SeriesTable.id).nullable()
    val status = enumerationByName<ResourceStatus>("status", 255)

    fun InsertStatement<Number>.from(resource: Resource) {
        this[id] = resource.resourceId.value
        this[title] = resource.title.value
        this[author] = resource.authorId.value
        this[releaseDate] = resource.releaseDate.value
        this[description] = resource.description?.value
        this[series] = resource.series?.value
        this[status] = resource.status
    }
}

class ResourceNotFoundException(resourceId: ResourceId) : BaseException("Resource ${resourceId.value} could not be found")
