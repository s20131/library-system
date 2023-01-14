package pja.s20131.librarysystem.domain.library

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LibraryService(
    val libraryRepository: LibraryRepository
) {
    fun getAllLibraries() = libraryRepository.getAll()
}
