package pja.s20131.librarysystem.user

import net.datafaker.Faker
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.CardNumber
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.Expiration
import pja.s20131.librarysystem.domain.user.model.IsActive
import pja.s20131.librarysystem.domain.user.model.Password
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.Username
import pja.s20131.librarysystem.storage.StorageDatabaseHelper
import java.time.Instant

@Component
class UserPreconditions(
    private val userDatabaseHelper: UserDatabaseHelper,
    private val storageDatabaseHelper: StorageDatabaseHelper,
    private val libraryCardDatabaseHelper: LibraryCardDatabaseHelper,
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
    ): Builder {
        val user = User(userId, firstName, lastName, email, username, password)
        userDatabaseHelper.insertUser(user)
        itemsInStorage.forEach { storageDatabaseHelper.insertToStorage(user.userId, it.first, it.second) }
        return Builder(user)
    }

    inner class Builder(private val user: User) {

        fun hasCard(cardNumber: CardNumber, expiration: Expiration, isActive: IsActive = IsActive(true)): Builder {
            libraryCardDatabaseHelper.insertCard(cardNumber, user.userId, expiration, isActive)
            return this
        }

        fun build(): User {
            return user
        }
    }
}
