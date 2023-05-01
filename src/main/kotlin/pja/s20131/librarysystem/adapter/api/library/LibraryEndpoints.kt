package pja.s20131.librarysystem.adapter.api.library

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.LibraryService

@RestController
@RequestMapping("/libraries")
class LibraryEndpoints(
    val libraryService: LibraryService
) {

    @GetMapping
    fun getLibraries(): List<GetLibraryResponse> =
        libraryService.getAllLibraries().toResponse()
}

private fun List<Library>.toResponse() = map { GetLibraryResponse(it.libraryId, it.libraryName, it.address) }
