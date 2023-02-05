package pja.s20131.librarysystem.adapter.api.library

import pja.s20131.librarysystem.domain.library.model.Address
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryName

data class GetLibraryResponse(val libraryName: LibraryName, val address: Address) {

    companion object {

        fun Library.toGetLibraryResponse() = GetLibraryResponse(this.libraryName, this.address)
    }
}
