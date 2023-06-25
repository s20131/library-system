package pja.s20131.librarysystem.adapter.database.resource

import net.postgis.jdbc.geometry.Point
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.exposed.stDistance
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.adapter.database.library.LibraryTable.toLibrary
import pja.s20131.librarysystem.domain.library.ResourceCopy
import pja.s20131.librarysystem.domain.library.model.Distance
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.port.CopyRepository
import pja.s20131.librarysystem.exception.BaseException

@Repository
class SqlCopyRepository : CopyRepository {

    override fun getAllBy(resourceId: ResourceId, userLocation: Point?): List<ResourceCopy> {
        return (CopyTable innerJoin LibraryTable)
            .select { CopyTable.resourceId eq resourceId.value }
            .applyOrderBy(userLocation)
            .map {
                ResourceCopy(
                    it.toLibrary(),
                    Available(it[CopyTable.available]),
                    if (userLocation != null) Distance(it[LibraryTable.location stDistance userLocation]) else null,
                )
            }
    }

    private fun Query.applyOrderBy(userLocation: Point?) = apply {
        if (userLocation != null) {
            adjustSlice { slice(it.fields + (LibraryTable.location stDistance userLocation)) }
                .orderBy(CopyTable.available eq 0 to SortOrder.ASC, (LibraryTable.location stDistance userLocation) to SortOrder.ASC)
        } else {
            orderBy(CopyTable.available to SortOrder.DESC)
        }
    }

    override fun getAvailability(resourceId: ResourceId, libraryId: LibraryId): Available {
        return (CopyTable innerJoin LibraryTable)
            .slice(CopyTable.available)
            .select { CopyTable.resourceId eq resourceId.value and (CopyTable.libraryId eq libraryId.value) }
            .singleOrNull()
            ?.let { Available(it[CopyTable.available]) } ?: throw CopyNotFoundException(resourceId, libraryId)
    }

    override fun increaseAvailability(resourceId: ResourceId, libraryId: LibraryId) {
        CopyTable.update({
            CopyTable.resourceId eq resourceId.value and (CopyTable.libraryId eq libraryId.value)
        }) {
            it[available] = available + 1
        }
    }

    override fun decreaseAvailability(resourceId: ResourceId, libraryId: LibraryId) {
        CopyTable.update({
            CopyTable.resourceId eq resourceId.value and (CopyTable.libraryId eq libraryId.value)
        }) {
            it[available] = available - 1
        }
    }
}

object CopyTable : Table("copy") {
    val libraryId = reference("library_id", LibraryTable)
    val resourceId = reference("resource_id", ResourceTable)
    val available = integer("available")
    override val primaryKey = PrimaryKey(libraryId, resourceId)
}

class CopyNotFoundException(resourceId: ResourceId, libraryId: LibraryId) :
    BaseException("Copy of resource ${resourceId.value} not found in library ${libraryId.value}")
