package pja.s20131.librarysystem.user

import com.github.javafaker.Faker
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

object UserGen {
    private val faker = Faker()

    fun user(
        userId: UserId = UserId.generate(),
        firstName: FirstName = FirstName(faker.name().firstName()),
        lastName: LastName = LastName(faker.name().lastName()),
        email: Email = Email(faker.internet().emailAddress()),
        login: Login = Login(faker.idNumber().valid()),
        password: Password = Password(faker.internet().password()),
    ) = User(userId, firstName, lastName, email, login, password)

    fun userSettings(
        sendEndOfRentalReminder: SendEndOfRentalReminder = SendEndOfRentalReminder(true),
        sendWhenAvailableReminder: SendWhenAvailableReminder = SendWhenAvailableReminder(true),
        kindleEmail: KindleEmail? = KindleEmail(faker.internet().emailAddress()),
    ) = UserSettings(sendEndOfRentalReminder, sendWhenAvailableReminder, kindleEmail)

    fun basicUserSettings() = userSettings(SendEndOfRentalReminder(false), SendWhenAvailableReminder(false), kindleEmail = null)
}
