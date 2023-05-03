package pja.s20131.librarysystem.user

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.user.UserSettingsTable
import pja.s20131.librarysystem.adapter.database.user.UserSettingsTable.toUserSettings
import pja.s20131.librarysystem.adapter.database.user.UserTable
import pja.s20131.librarysystem.adapter.database.user.UserTable.toUser
import pja.s20131.librarysystem.domain.user.model.SendEndOfRentalReminder
import pja.s20131.librarysystem.domain.user.model.SendWhenAvailableReminder
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.user.UserGen.userSettings

@Component
@Transactional
class UserDatabaseHelper {

    fun assertUserIsSaved(userId: UserId) {
        val user = UserTable
            .select { UserTable.id eq userId.value }
            .singleOrNull()
            ?.toUser()
        val settings = UserSettingsTable
            .innerJoin(UserTable)
            .select { UserSettingsTable.id eq userId.value }
            .singleOrNull()
            ?.toUserSettings()

        assertThat(user).isNotNull
        assertThat(user?.userId).isEqualTo(userId)
        assertThat(settings).isNotNull
    }

    fun insertUser(user: User) {
        UserTable.insert {
            it[id] = user.userId.value
            it[firstName] = user.firstName.value
            it[lastName] = user.lastName.value
            it[email] = user.email.value
            it[username] = user.username.value
            it[password] = user.password.value
        }
        insertUserSettings(
            user.userId,
            userSettings(SendEndOfRentalReminder(false), SendWhenAvailableReminder(false), kindleEmail = null)
        )
    }

    fun insertUserSettings(userId: UserId, userSettings: UserSettings) {
        UserSettingsTable.insert {
            it[id] = userId.value
            it[sendEndOfRentalReminder] = userSettings.sendEndOfRentalReminder.value
            it[sendWhenAvailableReminder] = userSettings.sendWhenAvailableReminder.value
            it[kindleEmail] = userSettings.kindleEmail?.value
        }
    }
}
