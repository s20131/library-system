package pja.s20131.librarysystem.domain.library.port

import pja.s20131.librarysystem.domain.library.model.Library

interface LibraryRepository {
    fun getAll(): List<Library>
}
