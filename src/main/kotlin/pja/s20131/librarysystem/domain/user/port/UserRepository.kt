package pja.s20131.librarysystem.domain.user.port

import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.model.Username

interface UserRepository {
    fun getBy(userId: UserId): User
    fun getSettings(userId: UserId): UserSettings
    fun findBy(email: Email): User?
    fun findBy(username: Username): User?
    fun insertUser(user: User)
}
