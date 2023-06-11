package pja.s20131.librarysystem.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.domain.user.AuthService
import pja.s20131.librarysystem.domain.user.BadCredentialsException
import pja.s20131.librarysystem.domain.user.EmailAlreadyExistsException
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.PasswordTooShortException

@SpringBootTest
class AuthServiceTests @Autowired constructor(
    private val authService: AuthService,
    private val given: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should authenticate and not give any authorities when user is a regular customer`() {
        val user = given.user.exists(withPasswordEncoding = true).build()
        val authentication = UsernamePasswordAuthenticationToken(user.username.value, user.password.value)

        val response = authService.authenticate(authentication)

        assertThat(response.authorities).doesNotContain(SimpleGrantedAuthority("ROLE_LIBRARIAN"))
    }

    @Test
    fun `should authenticate and give librarian role authority when user is librarian`() {
        val user = given.user.exists(withPasswordEncoding = true).build()
        given.library.exists().hasLibrarian(user.userId).build()
        val authentication = UsernamePasswordAuthenticationToken(user.username.value, user.password.value)

        val response = authService.authenticate(authentication)

        assertThat(response.authorities).containsExactly(SimpleGrantedAuthority("ROLE_LIBRARIAN"))
    }

    @Test
    fun `should throw an error when user tries to authenticate with wrong password`() {
        val user = given.user.exists(withPasswordEncoding = true).build()
        val authentication = UsernamePasswordAuthenticationToken(user.username.value, Password("p").value)

        assertThrows<BadCredentialsException> { authService.authenticate(authentication) }
    }

    @Test
    fun `should throw an error when user tries to authenticate with not existing username`() {
        val user = UserGen.user()
        val authentication = UsernamePasswordAuthenticationToken(user.username.value, user.password.value)

        assertThrows<BadCredentialsException> { authService.authenticate(authentication) }
    }

    @Test
    fun `should save a user and add basic settings and active library card when registering a user`() {
        val dto = UserGen.registerUserDto()

        val userId = authService.register(dto)

        assert.user.isSaved(userId)
    }

    @Test
    fun `should throw an error when user tries to register and password is too short`() {
        val dto = UserGen.registerUserDto(password = Password("p"))

        assertThrows<PasswordTooShortException> { authService.register(dto) }
    }

    @Test
    fun `should throw an error when user tries to register and email is already saved in the database`() {
        val user = given.user.exists().build()
        val dto = UserGen.registerUserDto(email = user.email)

        assertThrows<EmailAlreadyExistsException> { authService.register(dto) }
    }
}
