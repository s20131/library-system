package pja.s20131.librarysystem.adapter.database.resource

import net.postgis.jdbc.geometry.Point
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.exposed.pgStDistance
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.adapter.database.library.LibraryTable.toLibrary
import pja.s20131.librarysystem.domain.library.ResourceCopy
import pja.s20131.librarysystem.domain.library.model.Distance
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.port.CopyRepository

@Repository
class SqlCopyRepository : CopyRepository {

    override fun getAllBy(resourceId: ResourceId, userLocation: Point?): List<ResourceCopy> {
        return (CopyTable innerJoin LibraryTable)
            .select { CopyTable.resourceId eq resourceId.value and (CopyTable.available greater 0) }
            .applyUserLocation(userLocation)
            .map {
                ResourceCopy(
                    it.toLibrary(),
                    Available(it[CopyTable.available]),
                    if (userLocation != null) Distance(it[pgStDistance(userLocation)]) else null,
                )
            }
    }

    private fun Query.applyUserLocation(userLocation: Point?) = apply {
        if (userLocation != null) {
            adjustSlice { slice(it.fields + pgStDistance(userLocation)) }
                .orderBy(pgStDistance(userLocation))
        }
    }
}


object CopyTable : Table("copy") {
    val libraryId = reference("library_id", LibraryTable)
    val resourceId = reference("resource_id", ResourceTable)
    val available = integer("available")
    override val primaryKey = PrimaryKey(libraryId, resourceId)
}
