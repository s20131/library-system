package pja.s20131.librarysystem.domain.resource

import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PenaltyService {

    // TODO via config
    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Warsaw")
    fun updatePenaltiesForResourceOverdue() {
        transaction {
            TransactionManager.current().exec("CALL update_penalties()")
        }
    }
}
