package pja.s20131.librarysystem.user

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.domain.user.AuthService

@SpringBootTest
class AuthServiceTests @Autowired constructor(
    private val authService: AuthService,
    private val userDatabaseHelper: UserDatabaseHelper,
) : BaseTestConfig() {

    @Test
    fun `should save a user and add basic settings when registering a user`() {
        val command = UserGen.registerUserCommand()

        val userId = authService.register(command)

        userDatabaseHelper.assertUserIsSaved(userId)
    }

}
