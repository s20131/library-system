package pja.s20131.librarysystem.adapter.database.library

import pja.s20131.librarysystem.domain.library.Library

interface LibraryRepository {
    fun getAll(): List<Library>
}
