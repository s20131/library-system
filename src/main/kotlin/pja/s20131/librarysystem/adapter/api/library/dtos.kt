package pja.s20131.librarysystem.adapter.api.library

import pja.s20131.librarysystem.domain.library.model.Address
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName

data class GetLibraryResponse(val libraryId: LibraryId, val libraryName: LibraryName, val address: Address)
