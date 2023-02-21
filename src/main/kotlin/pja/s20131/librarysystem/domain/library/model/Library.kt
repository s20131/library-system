package pja.s20131.librarysystem.domain.library.model

import java.util.UUID

data class Library(
    val libraryId: LibraryId,
    val libraryName: LibraryName,
    val address: Address
)

@JvmInline
value class LibraryId(val value: UUID) {
    companion object {
        fun generate() = LibraryId(UUID.randomUUID())
    }
}

@JvmInline
value class LibraryName(val value: String)
