package pja.s20131.librarysystem.domain.resource.utils

import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RentalJobService {

    @Scheduled(cron = "\${cron.revoke-awaiting-resources}", zone = "\${time.zone}")
    fun revokeAwaitingResources() {
        transaction {
            TransactionManager.current().exec("CALL revoke_awaiting_resources()")
        }
    }

    @Scheduled(cron = "\${cron.revoke-ebooks}", zone = "\${time.zone}")
    fun revokeEbooks() {
        transaction {
            TransactionManager.current().exec("CALL revoke_ebooks()")
        }
    }
}
