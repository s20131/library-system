package pja.s20131.librarysystem.user

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.adapter.database.user.UserRepository
import pja.s20131.librarysystem.adapter.database.user.UserSettingsTable
import pja.s20131.librarysystem.adapter.database.user.UserTable
import pja.s20131.librarysystem.user.UserGenerator.user
import pja.s20131.librarysystem.user.UserSettingsGenerator.basicUserSettings

@SpringBootTest
class UserRepositoryTests @Autowired constructor(
    val userRepository: UserRepository
) {

    @BeforeEach
    fun clear() {
        transaction {
            UserSettingsTable.deleteAll()
            UserTable.deleteAll()
        }
    }

    @Test
    fun `should get the user`() {
        val user = user()

        addUser(user)

        val response = transaction { userRepository.get(user.userId) }
        assertThat(response).isEqualTo(user.toBasicData())
    }

    @Test
    fun `should return null if the requested user doesn't exist`() {
        val user = user()

        val response = transaction { userRepository.get(user.userId) }
        assertThat(response).isEqualTo(null)
    }

    @Test
    fun `should add user settings when adding a user`() {
        val user = user()

        addUser(user)

        val response = transaction { userRepository.getSettings(user.userId) }
        assertThat(response).isEqualTo(basicUserSettings())
    }

}