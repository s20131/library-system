package pja.s20131.librarysystem.adapter.database.user

import java.util.UUID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.FirstName
import pja.s20131.librarysystem.domain.user.model.KindleEmail
import pja.s20131.librarysystem.domain.user.model.LastName
import pja.s20131.librarysystem.domain.user.model.SendEndOfRentalReminder
import pja.s20131.librarysystem.domain.user.model.SendWhenAvailableReminder
import pja.s20131.librarysystem.domain.user.model.UserBasicData
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.port.UserRepository

@Repository
class SqlUserRepository : UserRepository {

    override fun get(userId: UserId): UserBasicData? {
        return UserTable
            .select { UserTable.id eq userId.value }
            .singleOrNull()
            ?.toUserBasicData()
    }

    override fun getSettings(userId: UserId): UserSettings? {
        return UserSettingsTable
            .select { UserSettingsTable.id eq userId.value }
            .singleOrNull()
            ?.toUserSettings()
    }

}

private fun ResultRow.toUserBasicData() =
    UserBasicData(
        FirstName(this[UserTable.firstName]),
        LastName(this[UserTable.lastName]),
        Email(this[UserTable.email]),
    )

private fun ResultRow.toUserSettings() =
    UserSettings(
        SendEndOfRentalReminder(this[UserSettingsTable.sendEndOfRentalReminder]),
        SendWhenAvailableReminder(this[UserSettingsTable.sendWhenAvailableReminder]),
        this[UserSettingsTable.kindleEmail]?.let { KindleEmail(it) },
    )

object UserTable : UUIDTable("\"user\"") {
    val firstName = text("first_name")
    val lastName = text("last_name")
    val email = text("email")
    val login = text("login")
    val password = text("password")
}

object UserSettingsTable : IdTable<UUID>("user_settings") {
    override val id = reference("user_id", UserTable.id)
    override val primaryKey = PrimaryKey(id)
    val sendEndOfRentalReminder = bool("send_end_of_rental_reminder")
    val sendWhenAvailableReminder = bool("send_when_available_reminder")
    val kindleEmail = text("kindle_email").nullable()
}
