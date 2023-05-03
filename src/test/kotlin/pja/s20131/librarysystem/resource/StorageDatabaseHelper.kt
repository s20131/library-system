package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.StorageTable
import pja.s20131.librarysystem.adapter.database.resource.StorageTable.eqKey
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceType
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

    fun assertResourceIsSavedInStorage(userId: UserId, resourceId: ResourceId, resourceType: ResourceType) {
        val result = StorageTable
            .select { eqKey(userId, resourceId) }
            .single()

        assertThat(result[StorageTable.resourceId].value).isEqualTo(resourceId.value)
        assertThat(result[StorageTable.userId].value).isEqualTo(userId.value)
    }

}
