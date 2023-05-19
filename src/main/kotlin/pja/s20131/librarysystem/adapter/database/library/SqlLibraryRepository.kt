package pja.s20131.librarysystem.adapter.database.library

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.exposed.point
import pja.s20131.librarysystem.adapter.database.library.LibraryTable.toLibrary
import pja.s20131.librarysystem.domain.library.model.Address
import pja.s20131.librarysystem.domain.library.model.City
import pja.s20131.librarysystem.domain.library.model.Library
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.library.model.Location
import pja.s20131.librarysystem.domain.library.model.Postcode
import pja.s20131.librarysystem.domain.library.model.StreetName
import pja.s20131.librarysystem.domain.library.model.StreetNumber
import pja.s20131.librarysystem.domain.library.port.LibraryRepository
import pja.s20131.librarysystem.exception.BaseException

@Repository
class SqlLibraryRepository : LibraryRepository {

    override fun getAll(): List<Library> =
        LibraryTable.selectAll().map { it.toLibrary() }

    override fun get(libraryId: LibraryId): Library =
        LibraryTable.select { LibraryTable.id eq libraryId.value }.singleOrNull()?.toLibrary() ?: throw LibraryNotFoundException(libraryId)
}

object LibraryTable : UUIDTable("library") {
    val name = text("name")
    val streetName = text("street_name")
    val streetNumber = text("street_number")
    val postcode = text("postcode")
    val city = text("city")
    val location = point("location")

    fun ResultRow.toLibrary() =
        Library(
            LibraryId(this[id].value),
            LibraryName(this[name]),
            Address(
                StreetName(this[streetName]),
                StreetNumber(this[streetNumber]),
                Postcode(this[postcode]),
                City(this[city]),
                Location(this[location]),
            )
        )
}

class LibraryNotFoundException(libraryId: LibraryId) : BaseException("Library ${libraryId.value} was not found")
