package pja.s20131.librarysystem.adapter.api.library

import net.postgis.jdbc.geometry.Point
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.library.LibraryService
import pja.s20131.librarysystem.domain.library.ResourceCopy
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.AuthService

@RestController
@RequestMapping("/libraries")
class LibraryEndpoints(
    private val libraryService: LibraryService,
    private val authService: AuthService,
) {

    // TODO filtering
    @GetMapping
    fun getLibraries(): List<GetLibraryResponse> =
        libraryService.getAllLibraries().toResponse()

    @GetMapping("/librarian")
    @Secured("ROLE_LIBRARIAN")
    fun getLibrarianLibraries(): List<GetLibrarianLibrariesResponse> {
        return authService.withUserContext {
            libraryService.getLibrarianLibraries(it)
        }.toResponse()
    }

    @GetMapping("/copies/{resourceId}")
    fun getResourceCopiesInLibraries(
        @PathVariable resourceId: ResourceId,
        @RequestParam(required = false) latitude: Double?,
        @RequestParam(required = false) longitude: Double?,
    ): List<GetResourceCopyResponse> {
        // coordinates are reversed in PostGIS
        val userLocation = if (latitude == null || longitude == null) null else Point(longitude, latitude)
        return libraryService.getResourceCopiesInLibraries(resourceId, userLocation).toResponse()
    }

    @PutMapping("/{libraryId}/librarian")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_LIBRARIAN")
    fun updateSelectedLibrary(@PathVariable libraryId: LibraryId) {
        authService.withUserContext {
            libraryService.updateSelectedLibrary(libraryId, it)
        }
    }
}

private fun List<Library>.toResponse() = map { GetLibraryResponse(it.libraryId, it.libraryName, it.address) }

@JvmName("toSelectedLibraryResponse")
private fun List<Pair<Library, Boolean>>.toResponse() = map { GetLibrarianLibrariesResponse(it.first.libraryId, it.first.libraryName, it.second) }

@JvmName("toResourceCopyResponse")
private fun List<ResourceCopy>.toResponse() =
    map { GetResourceCopyResponse(it.library.libraryId, it.library.libraryName, it.library.address.toBasic(), it.available, it.distance) }
