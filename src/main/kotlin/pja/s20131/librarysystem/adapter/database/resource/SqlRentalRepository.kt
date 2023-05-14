package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.resource.BookTable.toBook
import pja.s20131.librarysystem.adapter.database.resource.EbookTable.toEbook
import pja.s20131.librarysystem.adapter.database.resource.RentalTable.toRentalHistory
import pja.s20131.librarysystem.adapter.database.user.UserTable
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.RentalHistory
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Penalty
import pja.s20131.librarysystem.domain.resource.model.Rental
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.port.RentalRepository
import pja.s20131.librarysystem.domain.user.model.UserId

@Repository
class SqlRentalRepository : RentalRepository {
    override fun getAllBy(userId: UserId): List<RentalHistory> {
        return (RentalTable innerJoin CopyTable innerJoin ResourceTable leftJoin BookTable leftJoin EbookTable innerJoin AuthorTable)
            .select { RentalTable.userId eq userId.value }
            .orderBy(RentalTable.finish)
            .map {
                try {
                    it.toBook()
                    it.toRentalHistory(ResourceType.BOOK)
                } catch (e: Exception) {
                    it.toEbook()
                    it.toRentalHistory(ResourceType.EBOOK)
                }
            }
    }

    override fun save(rental: Rental) {
        RentalTable.insert {
            it[userId] = rental.userId.value
            it[libraryId] = rental.libraryId.value
            it[resourceId] = rental.resourceId.value
            it[start] = rental.rentalPeriod.start
            it[finish] = rental.rentalPeriod.finish
            it[status] = rental.rentalStatus
            it[penalty] = rental.penalty?.value
        }
    }

}

object RentalTable : Table("rental") {
    val userId = reference("user_id", UserTable)
    val libraryId = reference("library_id", CopyTable.libraryId)
    val resourceId = reference("resource_id", CopyTable.resourceId)
    val start = timestamp("start")
    val finish = timestamp("finish")
    val status = enumerationByName("status", 255, RentalStatus::class)
    val penalty = decimal("penalty", 10, 2).nullable()
    override val primaryKey = PrimaryKey(userId, libraryId, resourceId)

    fun ResultRow.toRental() = Rental(
        UserId(this[userId].value),
        ResourceId(this[resourceId].value),
        LibraryId(this[libraryId].value),
        RentalPeriod(this[start], this[finish]),
        this[status],
        this[penalty]?.let { Penalty(it) },
    )

    fun ResultRow.toRentalHistory(resourceType: ResourceType) = RentalHistory(
        LibraryId(this[libraryId].value),
        ResourceBasicData(
            ResourceId(this[resourceId].value),
            Title(this[ResourceTable.title]),
        ),
        AuthorBasicData(
            FirstName(this[AuthorTable.firstName]),
            LastName(this[AuthorTable.lastName]),
        ),
        RentalPeriod(this[start], this[finish]).startDate,
        this[status],
        resourceType,
    )
}
