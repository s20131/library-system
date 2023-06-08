package pja.s20131.librarysystem.domain.user.model

import pja.s20131.librarysystem.exception.BaseException
import java.time.Instant

data class LibraryCard(
    val cardNumber: CardNumber,
    val userId: UserId,
    val expiration: Expiration,
    val isActive: IsActive,
) {
    fun checkIfActive() {
        if (!isActive.value) throw CardExpiredException(cardNumber)
    }
}

@JvmInline
value class CardNumber(val value: Long)

@JvmInline
value class Expiration(val value: Instant)

@JvmInline
value class IsActive(val value: Boolean)

class CardExpiredException(number: CardNumber) : BaseException("Library card ${number.value} has expired")
