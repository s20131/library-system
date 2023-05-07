package pja.s20131.librarysystem.user

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.assertions.Assertions
import pja.s20131.librarysystem.domain.user.AuthService

@SpringBootTest
class AuthServiceTests @Autowired constructor(
    private val authService: AuthService,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should save a user and add basic settings when registering a user`() {
        val command = UserGen.registerUserCommand()

        val userId = authService.register(command)

        assert.user.isSaved(userId)
    }

}
