package pja.s20131.librarysystem.reservation

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.adapter.database.resource.ReservationTable
import pja.s20131.librarysystem.adapter.database.resource.ReservationTable.toReservation
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId

@Component
class ReservationAssertions {

    fun isSaved(resourceId: ResourceId, libraryId: LibraryId, userId: UserId) {
        val expectedReservation = transaction {
            ReservationTable.select {
                ReservationTable.resourceId eq resourceId.value and (ReservationTable.libraryId eq libraryId.value) and (ReservationTable.userId eq userId.value)
            }.singleOrNull()?.toReservation()
        }

        assertThat(expectedReservation).isNotNull
    }

    fun isNotSaved(resourceId: ResourceId, userId: UserId) {
        val expectedReservation = transaction {
            ReservationTable.select {
                ReservationTable.resourceId eq resourceId.value and (ReservationTable.userId eq userId.value)
            }.singleOrNull()
        }

        assertThat(expectedReservation).isNull()
    }
}
