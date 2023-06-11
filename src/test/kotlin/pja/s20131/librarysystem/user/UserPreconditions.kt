package pja.s20131.librarysystem.user

import net.datafaker.Faker
import org.springframework.security.crypto.password.PasswordEncoder
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
import pja.s20131.librarysystem.infrastracture.TestClock
import pja.s20131.librarysystem.storage.StorageDatabaseHelper
import java.time.Instant

@Component
class UserPreconditions(
    private val userDatabaseHelper: UserDatabaseHelper,
    private val storageDatabaseHelper: StorageDatabaseHelper,
    private val libraryCardDatabaseHelper: LibraryCardDatabaseHelper,
    private val passwordEncoder: PasswordEncoder,
    private val clock: TestClock,
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
        withPasswordEncoding: Boolean = false,
    ): Builder {
        val user = User(userId, firstName, lastName, email, username, password)
        if (withPasswordEncoding) {
            userDatabaseHelper.insertUser(user.copy(password = Password(passwordEncoder.encode(password.value))))
        } else {
            userDatabaseHelper.insertUser(user)
        }
        storageDatabaseHelper.batchInsertToStorage(user.userId, itemsInStorage)
        return Builder(user)
    }

    inner class Builder(private val user: User) {

        fun hasCard(cardNumber: CardNumber, expiration: Expiration = Expiration(clock.inDays(365)), isActive: IsActive = IsActive(true)): Builder {
            libraryCardDatabaseHelper.insertCard(cardNumber, user.userId, expiration, isActive)
            return this
        }

        fun build(): User {
            return user
        }
    }
}
