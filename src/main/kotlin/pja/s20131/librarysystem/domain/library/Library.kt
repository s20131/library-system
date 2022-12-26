package pja.s20131.librarysystem.domain.library

import java.util.UUID


data class Library (
    val libraryId: LibraryId,
    val libraryName: LibraryName,
    val address: Address
)

@JvmInline
value class LibraryId(val raw: UUID)

@JvmInline
value class LibraryName(val raw: String)
