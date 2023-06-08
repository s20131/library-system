package pja.s20131.librarysystem.adapter.database.user

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.user.LibraryCardTable.toLibraryCard
import pja.s20131.librarysystem.domain.user.model.CardNumber
import pja.s20131.librarysystem.domain.user.model.Expiration
import pja.s20131.librarysystem.domain.user.model.IsActive
import pja.s20131.librarysystem.domain.user.model.LibraryCard
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.port.LibraryCardRepository
import pja.s20131.librarysystem.exception.BaseException
import java.time.Instant
import java.time.temporal.ChronoUnit

@Repository
class SqlLibraryCardRepository : LibraryCardRepository {
    override fun getActive(userId: UserId): LibraryCard {
        return LibraryCardTable
            .innerJoin(UserTable)
            .select { UserTable.id eq userId.value and (LibraryCardTable.isActive eq true) }
            .singleOrNull()
            ?.toLibraryCard() ?: throw LibraryCardDoesNotExistException(userId)
    }

    override fun getActive(number: CardNumber): LibraryCard {
        return LibraryCardTable
            .innerJoin(UserTable)
            .select { LibraryCardTable.id eq number.value and (LibraryCardTable.isActive eq true) }
            .singleOrNull()
            ?.toLibraryCard() ?: throw LibraryCardDoesNotExistException(number)
    }

    override fun save(userId: UserId) {
        LibraryCardTable.insert {
            it[LibraryCardTable.userId] = userId.value
            it[expiration] = Instant.now().plus(365, ChronoUnit.DAYS)
            it[isActive] = true
        }
    }
}

object LibraryCardTable : LongIdTable("library_card", "number") {
    val userId = reference("user_id", UserTable)
    val expiration = timestamp("expiration")
    val isActive = bool("is_active")

    fun ResultRow.toLibraryCard() = LibraryCard(
        CardNumber(this[id].value),
        UserId(this[userId].value),
        Expiration(this[expiration]),
        IsActive(this[isActive])
    )
}

class LibraryCardDoesNotExistException : BaseException {
    constructor(number: CardNumber) : super("Library card ${number.value} does not exist")

    constructor(userId: UserId) : super("Library card for user ${userId.value} does not exist")
}
