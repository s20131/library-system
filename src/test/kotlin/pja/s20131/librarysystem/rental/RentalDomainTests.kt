package pja.s20131.librarysystem.rental

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.CannotReserveResourceException
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.ebook.EbookGen
import java.time.Instant

class RentalDomainTests {

    @Test
    fun `should throw exception when trying to reserve to borrow a not-book resource`() {
        val ebook = EbookGen.ebook()

        assertThrows<CannotReserveResourceException> { ebook.reserveToBorrow(UserId.generate(), LibraryId.generate(), Instant.now()) }
    }
}
