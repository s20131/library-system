package pja.s20131.librarysystem.resource

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.StorageTable
import pja.s20131.librarysystem.adapter.database.resource.StorageTable.eqKey
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId
import java.time.Instant

@Component
@Transactional
class StorageDatabaseHelper {

    fun insertToStorage(userId: UserId, resourceId: ResourceId, since: Instant = Instant.now()) {
        StorageTable.insert {
            it[this.userId] = userId.value
            it[this.resourceId] = resourceId.value
            it[this.since] = since
        }
    }

    fun getResultBy(userId: UserId, resourceId: ResourceId): ResultRow {
        return StorageTable
            .select { eqKey(userId, resourceId) }
            .single()
    }
}
