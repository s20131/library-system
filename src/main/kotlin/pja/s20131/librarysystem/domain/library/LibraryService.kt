package pja.s20131.librarysystem.domain.library

import org.springframework.stereotype.Service

@Service
class LibraryService(
    val repository: LibraryRepository
) {

    fun getAllLibraries() = repository.getAll()

}
