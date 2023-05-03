package pja.s20131.librarysystem.library

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pja.s20131.librarysystem.domain.library.model.InvalidPostcodePatternException
import pja.s20131.librarysystem.domain.library.model.Postcode

class LibraryDomainTests {

    @ParameterizedTest
    @ValueSource(strings = ["12345", "abc", "0-5555", "123-45"])
    fun `should forbid creating a postcode with invalid pattern`(postcode: String) {
        assertThrows<InvalidPostcodePatternException> { Postcode(postcode) }
    }
}
