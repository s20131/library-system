package pja.s20131.librarysystem.adapter.api.library

import pja.s20131.librarysystem.adapter.api.library.GetLibraryResponse.Companion.toResponse
import pja.s20131.librarysystem.domain.library.model.Address
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryName

data class GetLibraryResponse(val libraryName: LibraryName, val address: Address) {
    companion object {
        fun Library.toResponse() = GetLibraryResponse(this.libraryName, this.address)
    }
}

data class GetLibrariesResponse(val libraries: List<GetLibraryResponse>) {
    companion object {
        fun List<Library>.toResponse() = GetLibrariesResponse(this.map { it.toResponse() })
    }
}
