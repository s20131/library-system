package pja.s20131.librarysystem.user

import net.datafaker.Faker
import pja.s20131.librarysystem.domain.user.model.CardNumber
import pja.s20131.librarysystem.domain.user.model.Expiration
import pja.s20131.librarysystem.domain.user.model.IsActive
import pja.s20131.librarysystem.domain.user.model.LibraryCard
import pja.s20131.librarysystem.domain.user.model.UserId
import java.time.Instant

object LibraryCardGen {
    private val faker = Faker()

    fun card(
        userId: UserId,
        cardNumber: CardNumber = CardNumber(faker.number().numberBetween(1000L, 10000)),
        expiration: Expiration = Expiration(Instant.now()),
        isActive: IsActive = IsActive(true),
    ) = LibraryCard(cardNumber, userId, expiration, isActive)
}
