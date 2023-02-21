package pja.s20131.librarysystem.ebook

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.domain.resource.model.Size
import pja.s20131.librarysystem.domain.resource.model.Size.NegativeSizeException
import pja.s20131.librarysystem.ebook.EbookGen.ebook

@SpringBootTest
class EbookDomainTests {

    @Test
    fun `should return BAD_REQUEST when trying to insert ebook with size lower than 0`() {
        assertThatThrownBy { ebook(size = Size(-.1)) }
            .isInstanceOf(NegativeSizeException::class.java)
            .hasMessage("Size cannot be negative - given size=-0.1")
    }

}
