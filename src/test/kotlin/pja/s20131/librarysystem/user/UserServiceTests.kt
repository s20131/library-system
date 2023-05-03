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

@SpringBootTest
class UserServiceTests @Autowired constructor(
    val userService: UserService,
    val userDatabaseHelper: UserDatabaseHelper,
) : BaseTestConfig() {

    @Test
    fun `should get the user`() {
        val user = UserGen.user()
        userDatabaseHelper.insertUser(user)

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
        val user = UserGen.user()
        userDatabaseHelper.insertUser(user)

        val response = userService.getUserSettings(user.userId)

        assertThat(response).isEqualTo(UserSettings.basic())
    }

    @Test
    fun `should return NOT_FOUND if the requested user settings does not exist`() {
        val user = UserGen.user()

        assertThrows<UserNotFoundException> { userService.getUserSettings(user.userId) }
    }

}
