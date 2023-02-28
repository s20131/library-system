package pja.s20131.librarysystem.user

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.user.UserNotFoundException
import pja.s20131.librarysystem.adapter.database.user.UserSettingsTable
import pja.s20131.librarysystem.adapter.database.user.UserTable
import pja.s20131.librarysystem.domain.user.port.UserRepository
import pja.s20131.librarysystem.user.UserGen.basicUserSettings
import pja.s20131.librarysystem.user.UserGen.user

@SpringBootTest
@Transactional
class UserRepositoryTests @Autowired constructor(
    val userRepository: UserRepository,
    val userDatabaseHelper: UserDatabaseHelper,
) {

    @BeforeEach
    fun clear() {
        UserSettingsTable.deleteAll()
        UserTable.deleteAll()
    }

    @Test
    fun `should get the user`() {
        val user = user()
        userDatabaseHelper.insertUser(user)

        val response = userRepository.get(user.userId)

        assertThat(response).isEqualTo(user.toBasicData())
    }

    @Test
    fun `should return NOT_FOUND if the requested user doesn't exist`() {
        val user = user()

        assertThatThrownBy { userRepository.get(user.userId) }
            .isInstanceOf(UserNotFoundException::class.java)
            .hasMessage("User with id=${user.userId.value} not found")
    }

    @Test
    fun `should add user settings when adding a user`() {
        val user = user()

        // TODO test real implementation
        userDatabaseHelper.insertUser(user)

        val response = userRepository.getSettings(user.userId)
        assertThat(response).isEqualTo(basicUserSettings())
    }

}