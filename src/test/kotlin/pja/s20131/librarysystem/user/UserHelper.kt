package pja.s20131.librarysystem.user

import com.github.javafaker.Faker
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import pja.s20131.librarysystem.adapter.database.user.UserSettingsTable
import pja.s20131.librarysystem.adapter.database.user.UserTable
import pja.s20131.librarysystem.domain.user.Email
import pja.s20131.librarysystem.domain.user.FirstName
import pja.s20131.librarysystem.domain.user.KindleEmail
import pja.s20131.librarysystem.domain.user.LastName
import pja.s20131.librarysystem.domain.user.Login
import pja.s20131.librarysystem.domain.user.Password
import pja.s20131.librarysystem.domain.user.SendEndOfRentalReminder
import pja.s20131.librarysystem.domain.user.SendWhenAvailableReminder
import pja.s20131.librarysystem.domain.user.User
import pja.s20131.librarysystem.domain.user.UserId
import pja.s20131.librarysystem.domain.user.UserSettings
import pja.s20131.librarysystem.user.UserSettingsGenerator.userSettings

val faker = Faker()

object UserGenerator {
    fun user(
        userId: UserId = UserId.generate(),
        firstName: FirstName = FirstName(faker.name().firstName()),
        lastName: LastName = LastName(faker.name().lastName()),
        email: Email = Email(faker.internet().emailAddress()),
        login: Login = Login(faker.idNumber().valid()),
        password: Password = Password(faker.internet().password()),
    ) = User(userId, firstName, lastName, email, login, password)
}

object UserSettingsGenerator {
    fun userSettings(
        sendEndOfRentalReminder: SendEndOfRentalReminder = SendEndOfRentalReminder(true),
        sendWhenAvailableReminder: SendWhenAvailableReminder = SendWhenAvailableReminder(true),
        kindleEmail: KindleEmail? = KindleEmail(faker.internet().emailAddress()),
    ) = UserSettings(sendEndOfRentalReminder, sendWhenAvailableReminder, kindleEmail)

    fun basicUserSettings() = userSettings(SendEndOfRentalReminder(false), SendWhenAvailableReminder(false), kindleEmail = null)
}

fun addUser(user: User) {
    transaction {
        UserTable.insert {
            it[id] = user.userId.raw
            it[firstName] = user.firstName.raw
            it[lastName] = user.lastName.raw
            it[email] = user.email.raw
            it[login] = user.email.raw
            it[password] = user.password.raw
        }
        addUserSettings(
            user.userId,
            userSettings(SendEndOfRentalReminder(false), SendWhenAvailableReminder(false), kindleEmail = null)
        )
    }
}

fun addUserSettings(userId: UserId, userSettings: UserSettings) {
    transaction {
        UserSettingsTable.insert {
            it[id] = userId.raw
            it[sendEndOfRentalReminder] = userSettings.sendEndOfRentalReminder.raw
            it[sendWhenAvailableReminder] = userSettings.sendWhenAvailableReminder.raw
            it[kindleEmail] = userSettings.kindleEmail?.raw
        }
    }
}
