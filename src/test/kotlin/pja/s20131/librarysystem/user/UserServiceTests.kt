package pja.s20131.librarysystem.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.domain.user.UserService
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.port.UserNotFoundException
import pja.s20131.librarysystem.Preconditions

@SpringBootTest
class UserServiceTests @Autowired constructor(
    val userService: UserService,
    val assuming: Preconditions,
) : BaseTestConfig() {

    @Test
    fun `should get the user`() {
        val user = assuming.user.exists()

        val response = userService.getUser(user.userId)

        assertThat(response).isEqualTo(user.toBasicData())
    }

    @Test
    fun `should return NOT_FOUND if the requested user does not exist`() {
        val user = UserGen.user()

        assertThrows<UserNotFoundException> { userService.getUser(user.userId) }
    }

    @Test
    fun `should get basic user settings after adding a new user`() {
        val user = assuming.user.exists()

        val response = userService.getUserSettings(user.userId)

        assertThat(response).isEqualTo(UserSettings.basic())
    }

    @Test
    fun `should return NOT_FOUND if the requested user settings does not exist`() {
        val user = UserGen.user()

        assertThrows<UserNotFoundException> { userService.getUserSettings(user.userId) }
    }

}
