package pja.s20131.librarysystem.resource

import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.domain.resource.model.Resource

@Component
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

}
