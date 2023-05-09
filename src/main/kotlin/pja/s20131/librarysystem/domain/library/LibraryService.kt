package pja.s20131.librarysystem.domain.library

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.port.LibraryRepository
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.port.CopyRepository

@Service
@Transactional
class LibraryService(
    private val libraryRepository: LibraryRepository,
    private val copyRepository: CopyRepository,
) {
    fun getAllLibraries(): List<Library> = libraryRepository.getAll()

    fun getResourceCopiesInLibraries(resourceId: ResourceId): List<ResourceCopy> {
        return copyRepository.getAllBy(resourceId)
    }
}

data class ResourceCopy(
    val library: Library,
    val available: Available,
)
