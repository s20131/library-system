package pja.s20131.librarysystem.domain.user.port

import pja.s20131.librarysystem.domain.user.model.UserBasicData
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings

interface UserRepository {
    fun get(userId: UserId): UserBasicData?
    fun getSettings(userId: UserId): UserSettings?
}
