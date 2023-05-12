package pja.s20131.librarysystem.preconditions

import net.datafaker.Faker
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.Username
import pja.s20131.librarysystem.resource.StorageDatabaseHelper
import pja.s20131.librarysystem.user.UserDatabaseHelper
import java.time.Instant

@Component
class UserPreconditions(
    private val userDatabaseHelper: UserDatabaseHelper,
    private val storageDatabaseHelper: StorageDatabaseHelper,
) {
    private val faker = Faker()

    fun exists(
        userId: UserId = UserId.generate(),
        firstName: FirstName = FirstName(faker.name().firstName()),
        lastName: LastName = LastName(faker.name().lastName()),
        email: Email = Email(faker.internet().emailAddress()),
        username: Username = Username(faker.name().username()),
        password: Password = Password(faker.internet().password()),
        itemsInStorage: List<Pair<ResourceId, Instant>> = emptyList(),
    ): User {
        val user = User(userId, firstName, lastName, email, username, password)
        userDatabaseHelper.insertUser(user)
        itemsInStorage.forEach { storageDatabaseHelper.insertToStorage(user.userId, it.first, it.second) }
        return user
    }
}