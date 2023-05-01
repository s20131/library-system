package pja.s20131.librarysystem.domain.library

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.library.port.LibraryRepository

@Service
@Transactional
class LibraryService(
    val libraryRepository: LibraryRepository
) {
    fun getAllLibraries() = libraryRepository.getAll()
}
