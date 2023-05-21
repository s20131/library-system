package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.adapter.database.resource.StorageTable
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.storage.StorageDatabaseHelper

@Component
class ResourceAssertions(
    private val storageDatabaseHelper: StorageDatabaseHelper,

    ) {
    fun isSavedInStorage(userId: UserId, resourceId: ResourceId, resourceType: ResourceType) {
        val result = storageDatabaseHelper.findResultBy(userId, resourceId)

        assertThat(result).isNotNull
        assertThat(result!![StorageTable.resourceId].value).isEqualTo(resourceId.value)
        assertThat(result[StorageTable.userId].value).isEqualTo(userId.value)
    }

    fun isNotSavedInStorage(userId: UserId, resourceId: ResourceId) {
        val result = storageDatabaseHelper.findResultBy(userId, resourceId)

        assertThat(result).isNull()
    }
}

