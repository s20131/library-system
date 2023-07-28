package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.upsert
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.resource.BookTable.toBook
import pja.s20131.librarysystem.adapter.database.resource.CoverTable.toCover
import pja.s20131.librarysystem.adapter.database.resource.EbookTable.toEbook
import pja.s20131.librarysystem.domain.resource.model.Resource
import pja.s20131.librarysystem.domain.resource.model.ResourceCover
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

    override fun getCover(resourceId: ResourceId): ResourceCover {
        return CoverTable.select {
            CoverTable.id eq resourceId.value
        }.singleOrNull()
            ?.toCover() ?: throw CoverNotFoundException(resourceId)
    }

    override fun upsertCover(resourceId: ResourceId, resourceCover: ResourceCover) {
        CoverTable.upsert {
            it[id] = resourceId.value
            it[content] = ExposedBlob(resourceCover.content)
            it[mediaType] = resourceCover.mediaType.toString()
        }
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

object CoverTable : Table("cover") {
    val id = reference("id", ResourceTable)
    val content = blob("content")
    val mediaType = text("media_type")
    override val primaryKey = PrimaryKey(id)

    fun ResultRow.toCover() = ResourceCover(this[content].bytes, MediaType.valueOf(this[mediaType]))
}

class ResourceNotFoundException(resourceId: ResourceId) : BaseException("Resource ${resourceId.value} could not be found")

class CoverNotFoundException(resourceId: ResourceId) : BaseException("Cover for resource ${resourceId.value} was not found")
