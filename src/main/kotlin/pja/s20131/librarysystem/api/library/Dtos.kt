package pja.s20131.librarysystem.api.library

import pja.s20131.librarysystem.domain.library.Address
import pja.s20131.librarysystem.domain.library.Library
import pja.s20131.librarysystem.domain.library.LibraryName

data class GetLibraryResponse(val libraryName: LibraryName, val address: Address) {

    companion object {

        fun Library.toGetLibraryResponse() = GetLibraryResponse(this.libraryName, this.address)
    }
}
