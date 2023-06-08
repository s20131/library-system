package pja.s20131.librarysystem.domain.library

import net.postgis.jdbc.geometry.Point
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.library.model.Distance
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.port.LibrarianRepository
import pja.s20131.librarysystem.domain.library.port.LibraryRepository
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.port.CopyRepository
import pja.s20131.librarysystem.domain.user.model.UserId

@Service
@Transactional
class LibraryService(
    private val libraryRepository: LibraryRepository,
    private val librarianRepository: LibrarianRepository,
    private val copyRepository: CopyRepository,
) {
    fun getAllLibraries(): List<Library> =
        libraryRepository.getAll()

    fun getLibrarianLibraries(librarianId: UserId): List<Pair<Library, Boolean>> =
        libraryRepository.getAll(librarianId)

    fun getResourceCopiesInLibraries(resourceId: ResourceId, userLocation: Point?): List<ResourceCopy> {
        return copyRepository.getAllBy(resourceId, userLocation)
    }

    fun updateSelectedLibrary(libraryId: LibraryId, librarianId: UserId) {
        val selected = librarianRepository.getSelectedLibrary(librarianId)
        librarianRepository.updateSelectedLibrary(selected.libraryId, librarianId, isSelected = false)
        librarianRepository.updateSelectedLibrary(libraryId, librarianId, isSelected = true)
    }
}

data class ResourceCopy(
    val library: Library,
    val available: Available,
    val distance: Distance?,
)
