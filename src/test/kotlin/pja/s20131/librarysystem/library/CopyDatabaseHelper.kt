package pja.s20131.librarysystem.library

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.CopyTable
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.ResourceId

@Component
@Transactional
class CopyDatabaseHelper {

    fun insertCopy(libraryId: LibraryId, resourceId: ResourceId, available: Available = Available(2)) {
        CopyTable.insert {
            it[CopyTable.libraryId] = libraryId.value
            it[CopyTable.resourceId] = resourceId.value
            it[CopyTable.available] = available.value
        }
    }

    fun getCopiesCount(libraryId: LibraryId, resourceId: ResourceId): Int {
        return CopyTable
            .select { CopyTable.resourceId eq resourceId.value and (CopyTable.libraryId eq libraryId.value) }
            .single().let { it[CopyTable.available] }
    }
}
