package pja.s20131.librarysystem.user

import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.user.LibraryCardTable
import pja.s20131.librarysystem.domain.user.model.LibraryCard

@Component
@Transactional
class LibraryCardDatabaseHelper {
    fun insertCard(libraryCard: LibraryCard) {
        LibraryCardTable.insert {
            it[id] = libraryCard.cardNumber.value
            it[userId] = libraryCard.userId.value
            it[expiration] = libraryCard.expiration.value
            it[isActive] = libraryCard.isActive.value
        }
    }
}
