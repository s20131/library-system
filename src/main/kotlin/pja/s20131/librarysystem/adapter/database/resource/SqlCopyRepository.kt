package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.port.CopyRepository
import pja.s20131.librarysystem.domain.resource.ResourceCopy

@Repository
class SqlCopyRepository : CopyRepository {

    override fun getAllBy(resourceId: ResourceId): List<ResourceCopy> {
        return CopyTable
            .select { CopyTable.resourceId eq resourceId.value }
            .map {
                ResourceCopy(
                    LibraryId(it[CopyTable.libraryId].value),
                    Available(it[CopyTable.available])
                )
            }
    }

}

object CopyTable : UUIDTable("copy") {
    val resourceId = reference("resource_id", ResourceTable)
    val libraryId = reference("library_id", LibraryTable)
    val available = integer("available")
}
