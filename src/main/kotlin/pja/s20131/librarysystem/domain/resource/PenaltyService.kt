package pja.s20131.librarysystem.domain.resource

import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PenaltyService {

    // TODO via config
    @Scheduled(cron = "\${cron.update-penalties}", zone = "Europe/Warsaw")
    fun updatePenaltiesForResourceOverdue() {
        transaction {
            TransactionManager.current().exec("CALL update_penalties()")
        }
    }
}
