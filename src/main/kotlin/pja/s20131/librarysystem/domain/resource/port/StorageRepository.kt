package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.user.model.UserId

interface StorageRepository {
    fun getFromStorageBy(userId: UserId): List<StoredResource>
}
