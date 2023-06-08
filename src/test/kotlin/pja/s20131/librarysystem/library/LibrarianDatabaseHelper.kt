package pja.s20131.librarysystem.library

import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.library.LibrarianTable
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.user.model.UserId

@Component
@Transactional
class LibrarianDatabaseHelper {
    fun insertLibrarian(userId: UserId, libraryId: LibraryId, isSelected: Boolean) {
        LibrarianTable.insert {
            it[LibrarianTable.userId] = userId.value
            it[LibrarianTable.libraryId] = libraryId.value
            it[LibrarianTable.isSelected] = isSelected
        }
    }
}
