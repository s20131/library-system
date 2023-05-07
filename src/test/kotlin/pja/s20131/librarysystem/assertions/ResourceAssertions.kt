package pja.s20131.librarysystem.assertions

import org.assertj.core.api.Assertions
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.adapter.database.resource.StorageTable
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.resource.StorageDatabaseHelper

@Component
class ResourceAssertions(
    private val storageDatabaseHelper: StorageDatabaseHelper,

) {
    fun isSavedInStorage(userId: UserId, resourceId: ResourceId, resourceType: ResourceType) {
        val result = storageDatabaseHelper.getResultBy(userId, resourceId)

        Assertions.assertThat(result[StorageTable.resourceId].value).isEqualTo(resourceId.value)
        Assertions.assertThat(result[StorageTable.userId].value).isEqualTo(userId.value)
    }
}

