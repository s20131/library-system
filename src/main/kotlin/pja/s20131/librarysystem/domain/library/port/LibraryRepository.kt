package pja.s20131.librarysystem.domain.library.port

import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.user.model.UserId

interface LibraryRepository {
    fun getAll(): List<Library>
    fun getAll(librarianId: UserId): List<Pair<Library, Boolean>>
    fun get(libraryId: LibraryId): Library
}
