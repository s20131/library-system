package pja.s20131.librarysystem.domain.user.port

import pja.s20131.librarysystem.domain.user.model.CardNumber
import pja.s20131.librarysystem.domain.user.model.Email
import pja.s20131.librarysystem.domain.user.model.User
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.model.Username
import pja.s20131.librarysystem.exception.BaseException

interface UserRepository {
    fun getBy(userId: UserId): User = findBy(userId) ?: throw UserNotFoundException(userId)
    fun getBy(cardNumber: CardNumber): User
    fun getSettingsBy(userId: UserId): UserSettings
    fun findBy(userId: UserId): User?
    fun findBy(email: Email): User?
    fun findBy(username: Username): User?
    fun save(user: User)
    fun saveSettings(userId: UserId, userSettings: UserSettings)
}

class UserNotFoundException : BaseException {
    constructor(id: UserId) : super("User with id ${id.value} was not found")
    constructor(cardNumber: CardNumber) : super("User with card number ${cardNumber.value} was not found")
}

