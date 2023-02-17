package pja.s20131.librarysystem.user

import com.github.javafaker.Faker
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import pja.s20131.librarysystem.adapter.database.user.UserSettingsTable
import pja.s20131.librarysystem.adapter.database.user.UserTable
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.KindleEmail
import pja.s20131.librarysystem.domain.user.model.Login
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.SendEndOfRentalReminder
import pja.s20131.librarysystem.domain.user.model.SendWhenAvailableReminder
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
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
            it[id] = user.userId.value
            it[firstName] = user.firstName.value
            it[lastName] = user.lastName.value
            it[email] = user.email.value
            it[login] = user.email.value
            it[password] = user.password.value
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
            it[id] = userId.value
            it[sendEndOfRentalReminder] = userSettings.sendEndOfRentalReminder.value
            it[sendWhenAvailableReminder] = userSettings.sendWhenAvailableReminder.value
            it[kindleEmail] = userSettings.kindleEmail?.value
        }
    }
}
