package pja.s20131.librarysystem.domain.library

interface LibraryRepository {
    fun getAll(): List<Library>
}
