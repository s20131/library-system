package pja.s20131.librarysystem.domain.library

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.port.LibraryRepository

@Service
@Transactional
class LibraryService(
    private val libraryRepository: LibraryRepository
) {
    fun getAllLibraries(): List<Library> = libraryRepository.getAll()
}
