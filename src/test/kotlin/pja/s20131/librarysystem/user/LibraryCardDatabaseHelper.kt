package pja.s20131.librarysystem.user

import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.user.LibraryCardTable
import pja.s20131.librarysystem.domain.user.model.CardNumber
import pja.s20131.librarysystem.domain.user.model.Expiration
import pja.s20131.librarysystem.domain.user.model.IsActive
import pja.s20131.librarysystem.domain.user.model.UserId

@Component
@Transactional
class LibraryCardDatabaseHelper {
    fun insertCard(
        cardNumber: CardNumber,
        userId: UserId,
        expiration: Expiration,
        isActive: IsActive
    ) {
        LibraryCardTable.insert {
            it[id] = cardNumber.value
            it[LibraryCardTable.userId] = userId.value
            it[LibraryCardTable.expiration] = expiration.value
            it[LibraryCardTable.isActive] = isActive.value
        }
    }
}
