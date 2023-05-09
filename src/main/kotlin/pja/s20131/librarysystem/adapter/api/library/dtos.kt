package pja.s20131.librarysystem.adapter.api.library

import pja.s20131.librarysystem.domain.library.model.Address
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.resource.model.Available

data class GetLibraryResponse(val libraryId: LibraryId, val libraryName: LibraryName, val address: Address)

data class GetResourceCopyResponse(
    val libraryId: LibraryId,
    val libraryName: LibraryName,
    val address: Address,
    val available: Available,
)
