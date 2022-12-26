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
        Libraries.selectAll().map { it.toLibrary() }
}

private fun ResultRow.toLibrary() =
    Library(
        LibraryId(this[Libraries.id].value),
        LibraryName(this[Libraries.name]),
        Address(
            StreetName(this[Libraries.streetName]),
            StreetNumber(this[Libraries.streetNumber]),
            Postcode(this[Libraries.postcode]),
            City(this[Libraries.city])
        )
    )

object Libraries : UUIDTable() {
    val name = text("name")
    val streetName = text("street_name")
    val streetNumber = text("street_number")
    val postcode = text("postcode")
    val city = text("city")
}
