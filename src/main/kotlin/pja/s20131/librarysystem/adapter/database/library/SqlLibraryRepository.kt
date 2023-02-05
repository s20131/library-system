package pja.s20131.librarysystem.adapter.database.library

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.domain.library.model.Address
import pja.s20131.librarysystem.domain.library.model.City
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.library.model.Postcode
import pja.s20131.librarysystem.domain.library.model.StreetName
import pja.s20131.librarysystem.domain.library.model.StreetNumber
import pja.s20131.librarysystem.domain.library.port.LibraryRepository

@Repository
class SqlLibraryRepository : LibraryRepository {

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
