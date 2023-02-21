package pja.s20131.librarysystem.library

import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.domain.library.model.Library

@Component
@Transactional
class LibraryDatabaseHelper {

    fun insertLibrary(library: Library) {
        LibraryTable.insert {
            it[id] = library.libraryId.value
            it[name] = library.libraryName.value
            it[streetName] = library.address.streetName.value
            it[streetNumber] = library.address.streetNumber.value
            it[postcode] = library.address.postcode.value
            it[city] = library.address.city.value
        }
    }
}
