package pja.s20131.librarysystem.resource

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.CoverTable
import pja.s20131.librarysystem.adapter.database.resource.CoverTable.toCover
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.domain.resource.model.Resource
import pja.s20131.librarysystem.domain.resource.model.ResourceCover
import pja.s20131.librarysystem.domain.resource.model.ResourceId

@Component
@Transactional
class ResourceDatabaseHelper {

    fun insertResource(resource: Resource) =
        ResourceTable.insert {
            it[id] = resource.resourceId.value
            it[title] = resource.title.value
            it[author] = resource.authorId.value
            it[releaseDate] = resource.releaseDate.value
            it[description] = resource.description?.value
            it[series] = resource.series?.value
            it[status] = resource.status
        }

    fun getCover(resourceId: ResourceId) =
        CoverTable.select {
            CoverTable.id eq resourceId.value
        }.single().toCover()

    fun insertCover(resourceId: ResourceId, cover: ResourceCover) =
        CoverTable.insert {
            it[id] = resourceId.value
            it[content] = ExposedBlob(cover.content)
            it[mediaType] = cover.mediaType.toString()
        }
}
