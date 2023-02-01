package pja.s20131.librarysystem.adapter.database.user

import pja.s20131.librarysystem.domain.user.UserBasicData
import pja.s20131.librarysystem.domain.user.UserId
import pja.s20131.librarysystem.domain.user.UserSettings

interface UserRepository {
    fun get(userId: UserId): UserBasicData?
    fun getSettings(userId: UserId): UserSettings?
}
