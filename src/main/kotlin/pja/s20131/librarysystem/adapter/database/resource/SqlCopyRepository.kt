package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.adapter.database.library.LibraryTable.toLibrary
import pja.s20131.librarysystem.domain.library.ResourceCopy
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.port.CopyRepository

@Repository
class SqlCopyRepository : CopyRepository {

    override fun getAllBy(resourceId: ResourceId): List<ResourceCopy> {
        return (CopyTable innerJoin LibraryTable)
            .select { CopyTable.resourceId eq resourceId.value }
            .map {
                ResourceCopy(
                    it.toLibrary(),
                    Available(it[CopyTable.available])
                )
            }
    }
}

object CopyTable : Table("copy") {
    val libraryId = reference("library_id", LibraryTable)
    val resourceId = reference("resource_id", ResourceTable)
    val available = integer("available")
    override val primaryKey = PrimaryKey(libraryId, resourceId)
}
