package pja.s20131.librarysystem.rental

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.RentalTable
import pja.s20131.librarysystem.adapter.database.resource.RentalTable.toRental
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.Penalty
import pja.s20131.librarysystem.domain.resource.model.Rental
import pja.s20131.librarysystem.domain.resource.model.RentalId
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId
import java.time.Instant

@Transactional
@Component
class RentalDatabaseHelper {
    fun findBy(rentalId: RentalId): Rental? {
        return RentalTable.select {
            RentalTable.id eq rentalId.value
        }.singleOrNull()?.toRental()
    }

    fun findBy(resourceId: ResourceId, userId: UserId, libraryId: LibraryId): Rental? {
        return RentalTable.select {
            RentalTable.resourceId eq resourceId.value and (RentalTable.userId eq userId.value) and (RentalTable.libraryId eq libraryId.value)
        }.singleOrNull()?.toRental()
    }

    fun insertRental(
        userId: UserId,
        resourceId: ResourceId,
        libraryId: LibraryId,
        rentalPeriod: RentalPeriod,
        rentalStatus: RentalStatus,
        penalty: Penalty? = null,
    ): Rental {
        val rental = Rental(RentalId.generate(), userId, resourceId, libraryId, rentalPeriod, rentalStatus, penalty)
        RentalTable.insert {
            it[RentalTable.id] = rental.rentalId.value
            it[RentalTable.userId] = rental.userId.value
            it[RentalTable.resourceId] = rental.resourceId.value
            it[RentalTable.libraryId] = rental.libraryId.value
            it[RentalTable.start] = rental.rentalPeriod.start
            it[RentalTable.finish] = rental.rentalPeriod.finish
            it[RentalTable.status] = rental.rentalStatus
            it[RentalTable.penalty] = rental.penalty?.value
        }
        return rental
    }
}
