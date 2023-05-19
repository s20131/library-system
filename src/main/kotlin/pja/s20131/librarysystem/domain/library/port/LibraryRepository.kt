package pja.s20131.librarysystem.domain.library.port

import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId

interface LibraryRepository {
    fun getAll(): List<Library>
    fun get(libraryId: LibraryId): Library
}
