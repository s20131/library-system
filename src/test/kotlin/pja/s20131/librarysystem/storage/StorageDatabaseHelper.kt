package pja.s20131.librarysystem.storage

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert
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

    fun batchInsertToStorage(userId: UserId, resources: List<Pair<ResourceId, Instant>>) {
        StorageTable.batchInsert(resources) { resourceIdToSince ->
            this[StorageTable.userId] = userId.value
            this[StorageTable.resourceId] = resourceIdToSince.first.value
            this[StorageTable.since] = resourceIdToSince.second
        }
    }

    fun findResultBy(userId: UserId, resourceId: ResourceId): ResultRow? {
        return StorageTable
            .select { eqKey(userId, resourceId) }
            .singleOrNull()
    }
}
