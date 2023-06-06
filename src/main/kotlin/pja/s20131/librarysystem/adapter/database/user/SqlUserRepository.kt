package pja.s20131.librarysystem.adapter.database.user

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.adapter.database.user.UserSettingsTable.toUserSettings
import pja.s20131.librarysystem.adapter.database.user.UserTable.toUser
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.KindleEmail
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.SendEndOfRentalReminder
import pja.s20131.librarysystem.domain.user.model.SendWhenAvailableReminder
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.model.Username
import pja.s20131.librarysystem.domain.user.port.UserNotFoundException
import pja.s20131.librarysystem.domain.user.port.UserRepository
import java.util.UUID

@Repository
class SqlUserRepository : UserRepository {

    override fun getSettingsBy(userId: UserId): UserSettings {
        return UserSettingsTable
            .select { UserSettingsTable.id eq userId.value }
            .singleOrNull()
            ?.toUserSettings() ?: throw UserNotFoundException(userId)
    }

    override fun findBy(userId: UserId): User? {
        return UserTable
            .select { UserTable.id eq userId.value }
            .singleOrNull()
            ?.toUser()
    }

    override fun findBy(email: Email): User? {
        return UserTable
            .select { UserTable.email eq email.value }
            .singleOrNull()
            ?.toUser()
    }

    override fun findBy(username: Username): User? {
        return UserTable
            .select { UserTable.username eq username.value }
            .singleOrNull()
            ?.toUser()
    }

    override fun save(user: User) {
        UserTable.insert {
            it[id] = user.userId.value
            it[firstName] = user.firstName.value
            it[lastName] = user.lastName.value
            it[email] = user.email.value
            it[username] = user.username.value
            it[password] = user.password.value
        }
    }

    override fun saveSettings(userId: UserId, userSettings: UserSettings) {
        UserSettingsTable.insert {
            it[id] = userId.value
            it[sendEndOfRentalReminder] = userSettings.sendEndOfRentalReminder.value
            it[sendWhenAvailableReminder] = userSettings.sendWhenAvailableReminder.value
            it[kindleEmail] = userSettings.kindleEmail?.value
        }
    }

    override fun isLibrarian(userId: UserId): Boolean {
        return (LibrarianTable innerJoin UserTable)
            .select { LibrarianTable.userId eq userId.value }
            .empty().not()
    }
}

object UserTable : UUIDTable("\"user\"") {
    val firstName = text("first_name")
    val lastName = text("last_name")
    val email = text("email")
    val username = text("username")
    val password = text("password")

    fun ResultRow.toUser() = User(
        UserId(this[id].value),
        FirstName(this[firstName]),
        LastName(this[lastName]),
        Email(this[email]),
        Username(this[username]),
        Password(this[password]),
    )
}

object UserSettingsTable : IdTable<UUID>("user_settings") {
    override val id = reference("user_id", UserTable)
    override val primaryKey = PrimaryKey(id)
    val sendEndOfRentalReminder = bool("send_end_of_rental_reminder")
    val sendWhenAvailableReminder = bool("send_when_available_reminder")
    val kindleEmail = text("kindle_email").nullable()

    fun ResultRow.toUserSettings() = UserSettings(
        SendEndOfRentalReminder(this[sendEndOfRentalReminder]),
        SendWhenAvailableReminder(this[sendWhenAvailableReminder]),
        this[kindleEmail]?.let { KindleEmail(it) },
    )
}

object LibrarianTable : Table("librarian") {
    val userId = reference("user_id", UserTable)
    val libraryId = reference("library_id", LibraryTable)
    override val primaryKey = PrimaryKey(userId, libraryId)
}
