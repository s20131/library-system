package pja.s20131.librarysystem.adapter.library

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.domain.library.Address
import pja.s20131.librarysystem.domain.library.City
import pja.s20131.librarysystem.domain.library.Library
import pja.s20131.librarysystem.domain.library.LibraryId
import pja.s20131.librarysystem.domain.library.LibraryName
import pja.s20131.librarysystem.domain.library.LibraryRepository
import pja.s20131.librarysystem.domain.library.Postcode
import pja.s20131.librarysystem.domain.library.StreetName
import pja.s20131.librarysystem.domain.library.StreetNumber

@Repository
class LibraryRepository : LibraryRepository {

    override fun getAll(): List<Library> =
        LibraryTable.selectAll().map { it.toLibrary() }
}

private fun ResultRow.toLibrary() =
    Library(
        LibraryId(this[LibraryTable.id].value),
        LibraryName(this[LibraryTable.name]),
        Address(
            StreetName(this[LibraryTable.streetName]),
            StreetNumber(this[LibraryTable.streetNumber]),
            Postcode(this[LibraryTable.postcode]),
            City(this[LibraryTable.city])
        )
    )

object LibraryTable : UUIDTable("library") {
    val name = text("name")
    val streetName = text("street_name")
    val streetNumber = text("street_number")
    val postcode = text("postcode")
    val city = text("city")
}
