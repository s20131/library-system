package pja.s20131.librarysystem.ebook

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pja.s20131.librarysystem.domain.resource.model.NegativeSizeException
import pja.s20131.librarysystem.domain.resource.model.Size
import pja.s20131.librarysystem.ebook.EbookGen.ebook

class EbookDomainTests {

    @Test
    fun `should return exception when trying to insert ebook with size lower than 0`() {
        assertThrows<NegativeSizeException> { ebook(size = Size(-1)) }
    }
}
