package pja.s20131.librarysystem.api.library

import java.util.UUID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.library.Address
import pja.s20131.librarysystem.domain.library.City
import pja.s20131.librarysystem.domain.library.Library
import pja.s20131.librarysystem.domain.library.LibraryId
import pja.s20131.librarysystem.domain.library.LibraryName
import pja.s20131.librarysystem.domain.library.LibraryService
import pja.s20131.librarysystem.domain.library.Postcode
import pja.s20131.librarysystem.domain.library.StreetName
import pja.s20131.librarysystem.domain.library.StreetNumber

@RestController
@RequestMapping("/libraries")
class LibraryEndpoints(
    val libraryService: LibraryService
) {

    @GetMapping
    fun getLibraries() = listOf(
        Library(LibraryId(UUID.randomUUID()), LibraryName("Biblioteka Publiczna Wesoła - 1"), Address(StreetName("Ulica"), StreetNumber("1"), Postcode("05-077"), City("Warszawa"))),
        Library(LibraryId(UUID.randomUUID()), LibraryName("Biblioteka Publiczna Wesoła - 2"), Address(StreetName("Ulica"), StreetNumber("2a"), Postcode("05-077"), City("Warszawa"))),
        Library(LibraryId(UUID.randomUUID()), LibraryName("Biblioteka Publiczna Wesoła - 3"), Address(StreetName("Ulica"), StreetNumber("3b"), Postcode("05-077"), City("Warszawa"))),
    )
}
