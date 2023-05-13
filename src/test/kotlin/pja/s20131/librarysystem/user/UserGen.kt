package pja.s20131.librarysystem.user

import net.datafaker.Faker
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.user.RegisterUserDto
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.KindleEmail
import pja.s20131.librarysystem.domain.user.model.Username
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
        username: Username = Username(faker.name().username()),
        password: Password = Password(faker.internet().password()),
    ) = User(userId, firstName, lastName, email, username, password)

    fun userSettings(
        sendEndOfRentalReminder: SendEndOfRentalReminder = SendEndOfRentalReminder(true),
        sendWhenAvailableReminder: SendWhenAvailableReminder = SendWhenAvailableReminder(true),
        kindleEmail: KindleEmail? = KindleEmail(faker.internet().emailAddress()),
    ) = UserSettings(sendEndOfRentalReminder, sendWhenAvailableReminder, kindleEmail)

    fun registerUserDto(
        firstName: FirstName = FirstName(faker.name().firstName()),
        lastName: LastName = LastName(faker.name().lastName()),
        email: Email = Email(faker.internet().emailAddress()),
        username: Username = Username(faker.name().username()),
        password: Password = Password(faker.internet().password()),
    ) = RegisterUserDto(firstName, lastName, email, username, password)
}
