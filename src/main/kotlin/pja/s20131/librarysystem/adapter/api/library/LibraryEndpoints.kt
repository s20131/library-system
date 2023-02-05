package pja.s20131.librarysystem.adapter.api.library

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.adapter.api.library.GetLibraryResponse.Companion.toGetLibraryResponse
import pja.s20131.librarysystem.domain.library.port.LibraryService

@RestController
@RequestMapping("/libraries")
class LibraryEndpoints(
    val libraryService: LibraryService
) {

    @GetMapping
    fun getLibraries(): List<GetLibraryResponse> =
        libraryService.getAllLibraries().map { it.toGetLibraryResponse() }
}
