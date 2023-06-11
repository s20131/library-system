package pja.s20131.librarysystem.domain.user.model

import java.time.Instant

data class LibraryCard(
    val cardNumber: CardNumber,
    val userId: UserId,
    val expiration: Expiration,
    val isActive: IsActive,
)

@JvmInline
value class CardNumber(val value: Long)

@JvmInline
value class Expiration(val value: Instant)

@JvmInline
value class IsActive(val value: Boolean)
