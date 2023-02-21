package pja.s20131.librarysystem.user

import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.user.UserSettingsTable
import pja.s20131.librarysystem.adapter.database.user.UserTable
import pja.s20131.librarysystem.domain.user.model.SendEndOfRentalReminder
import pja.s20131.librarysystem.domain.user.model.SendWhenAvailableReminder
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.user.UserGen.userSettings

@Component
@Transactional
class UserDatabaseHelper {

    fun insertUser(user: User) {
        UserTable.insert {
            it[id] = user.userId.value
            it[firstName] = user.firstName.value
            it[lastName] = user.lastName.value
            it[email] = user.email.value
            it[login] = user.email.value
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
