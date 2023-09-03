package pja.s20131.librarysystem.adapter.database.library

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.library.LibraryTable.toLibrary
import pja.s20131.librarysystem.adapter.database.user.UserTable
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.port.LibrarianRepository
import pja.s20131.librarysystem.domain.user.model.UserId

@Repository
class SqlLibrarianRepository : LibrarianRepository {
    override fun getSelectedLibrary(librarianId: UserId): Library {
        return LibraryTable
            .innerJoin(LibrarianTable)
            .select { LibrarianTable.isSelected eq true }
            .singleOrNull()
            ?.toLibrary() ?: throw UserHasNotSelectedLibraryException(librarianId)
    }

    override fun updateSelectedLibrary(libraryId: LibraryId, librarianId: UserId, isSelected: Boolean) {
        LibrarianTable
            .innerJoin(LibraryTable)
            .update({ LibrarianTable.userId eq librarianId.value and (LibrarianTable.libraryId eq libraryId.value) }) {
                it[LibrarianTable.isSelected] = isSelected
            }
    }

    override fun isLibrarian(userId: UserId): Boolean {
        return (LibrarianTable innerJoin UserTable)
            .select { LibrarianTable.userId eq userId.value }
            .empty().not()
    }

    override fun isLibrarianOf(userId: UserId, libraryId: LibraryId): Boolean {
        return (LibrarianTable innerJoin UserTable innerJoin LibraryTable)
            .select { LibrarianTable.userId eq userId.value and (LibrarianTable.libraryId eq libraryId.value) }
            .empty().not()
    }
}

object LibrarianTable : Table("librarian") {
    val userId = reference("user_id", UserTable)
    val libraryId = reference("library_id", LibraryTable)
    val isSelected = bool("is_selected")
    override val primaryKey = PrimaryKey(userId, libraryId)
}
