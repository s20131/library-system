package pja.s20131.librarysystem.domain.resource.utils

import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pja.s20131.librarysystem.domain.resource.model.RentalId

@Service
class PenaltyService {

    @Scheduled(cron = "\${cron.update-penalties}", zone = "\${time.zone}")
    fun updatePenaltiesForResourceOverdue() {
        transaction {
            TransactionManager.current().exec("CALL update_penalties()")
        }
    }

    fun addPenaltyForResourceOverdue(rentalId: RentalId) {
        transaction {
            TransactionManager.current().exec(
                "UPDATE rental SET status = 'PROLONGED', penalty = get_penalty() WHERE id = (?)",
                listOf(LongColumnType() to rentalId.value)
            )
        }
    }
}
