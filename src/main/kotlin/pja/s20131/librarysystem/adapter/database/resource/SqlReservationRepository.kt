package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.resource.BookTable.toBook
import pja.s20131.librarysystem.adapter.database.resource.EbookTable.toEbook
import pja.s20131.librarysystem.adapter.database.resource.ReservationTable.toReservation
import pja.s20131.librarysystem.adapter.database.resource.ReservationTable.toReservationHistory
import pja.s20131.librarysystem.adapter.database.user.UserTable
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.ReservationHistory
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Reservation
import pja.s20131.librarysystem.domain.resource.model.ReservationPeriod
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.port.ReservationRepository
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.exception.BaseException
import java.time.Instant

@Repository
class SqlReservationRepository : ReservationRepository {
    override fun getAllBy(userId: UserId): List<ReservationHistory> {
        return (ReservationTable innerJoin CopyTable innerJoin ResourceTable leftJoin BookTable leftJoin EbookTable innerJoin AuthorTable)
            .select { ReservationTable.userId eq userId.value }
            .orderBy(ReservationTable.start)
            .map {
                try {
                    it.toBook()
                    it.toReservationHistory(ResourceType.BOOK)
                } catch (e: Exception) {
                    it.toEbook()
                    it.toReservationHistory(ResourceType.EBOOK)
                }
            }
    }

    override fun getCurrentlyActiveBy(resourceId: ResourceId, userId: UserId, now: Instant): Reservation {
        return ReservationTable
            .select { ReservationTable.resourceId eq resourceId.value and (ReservationTable.userId eq userId.value) and (ReservationTable.finish greater now) }
            .singleOrNull()
            ?.toReservation() ?: throw ReservationNotFoundException(resourceId, userId)
    }

    override fun upsert(reservation: Reservation) {
        ReservationTable.upsert {
            it[userId] = reservation.userId.value
            it[libraryId] = reservation.libraryId.value
            it[resourceId] = reservation.resourceId.value
            it[start] = reservation.reservationPeriod.start
            it[finish] = reservation.reservationPeriod.finish
        }
    }

    override fun delete(resourceId: ResourceId, userId: UserId) {
        ReservationTable.deleteWhere {
            ReservationTable.resourceId eq resourceId.value and (ReservationTable.userId eq userId.value)
        }
    }

    override fun isCurrentlyReserved(resourceId: ResourceId, libraryId: LibraryId, userId: UserId, now: Instant): Boolean {
        return ReservationTable
            .select {
                ReservationTable.resourceId eq resourceId.value and
                        (ReservationTable.userId eq userId.value) and
                        (ReservationTable.libraryId eq libraryId.value) and
                        betweenStartAndFinish(now)
            }.empty().not()
    }

    override fun countCurrentlyReservedPerLibrary(resourceId: ResourceId, libraryId: LibraryId, now: Instant): Long {
        return ReservationTable
            .select {
                ReservationTable.resourceId eq resourceId.value and
                        (ReservationTable.libraryId eq libraryId.value) and
                        betweenStartAndFinish(now)
            }.count()
    }

    private fun SqlExpressionBuilder.betweenStartAndFinish(now: Instant) =
        (ReservationTable.start lessEq now) and (ReservationTable.finish greaterEq now)
}

object ReservationTable : Table("reservation") {
    val userId = reference("user_id", UserTable)
    val resourceId = reference("resource_id", CopyTable.resourceId)
    val libraryId = reference("library_id", CopyTable.libraryId)
    val start = timestamp("start")
    val finish = timestamp("finish")
    override val primaryKey = PrimaryKey(userId, resourceId)

    fun ResultRow.toReservation() = Reservation(
        UserId(this[userId].value),
        ResourceId(this[resourceId].value),
        LibraryId(this[libraryId].value),
        ReservationPeriod(this[start], this[finish]),
    )

    fun ResultRow.toReservationHistory(resourceType: ResourceType) = ReservationHistory(
        LibraryId(this[libraryId].value),
        ResourceBasicData(
            ResourceId(this[resourceId].value),
            Title(this[ResourceTable.title]),
        ),
        AuthorBasicData(
            FirstName(this[AuthorTable.firstName]),
            LastName(this[AuthorTable.lastName]),
        ),
        ReservationPeriod(this[start], this[finish]).finishDate,
        resourceType,
    )
}

class ReservationNotFoundException(resourceId: ResourceId, userId: UserId) :
    BaseException("Reservation of resource ${resourceId.value} for user ${userId.value} was not found")
