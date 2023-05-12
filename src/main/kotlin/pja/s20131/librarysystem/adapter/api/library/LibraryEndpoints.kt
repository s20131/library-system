package pja.s20131.librarysystem.adapter.api.library

import net.postgis.jdbc.geometry.Point
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.library.LibraryService
import pja.s20131.librarysystem.domain.library.ResourceCopy
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.resource.model.ResourceId

@RestController
@RequestMapping("/libraries")
class LibraryEndpoints(
    val libraryService: LibraryService
) {

    @GetMapping
    fun getLibraries(): List<GetLibraryResponse> =
        libraryService.getAllLibraries().toResponse()

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
}

private fun List<Library>.toResponse() = map { GetLibraryResponse(it.libraryId, it.libraryName, it.address) }

// TODO
@JvmName("toResourceCopyResponse")
private fun List<ResourceCopy>.toResponse() =
    map { GetResourceCopyResponse(it.library.libraryId, it.library.libraryName, it.library.address.toBasic(), it.available, it.distance) }
