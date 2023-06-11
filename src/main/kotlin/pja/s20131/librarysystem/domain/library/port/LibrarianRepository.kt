package pja.s20131.librarysystem.domain.library.port

import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.user.model.UserId

interface LibrarianRepository {
    fun getSelectedLibrary(librarianId: UserId): Library
    fun updateSelectedLibrary(libraryId: LibraryId, librarianId: UserId, isSelected: Boolean)
    fun isLibrarian(userId: UserId): Boolean
    fun isLibrarianOf(userId: UserId, libraryId: LibraryId): Boolean
}