package pja.s20131.librarysystem.domain.user

import org.springframework.stereotype.Service
import pja.s20131.librarysystem.adapter.database.user.UserRepository

@Service
class UserService(
    val userRepository: UserRepository
) {

    fun getUser(userId: UserId): UserBasicData {
        return userRepository.get(userId) ?: throw RuntimeException("User not found")
    }

    fun getUserSettings(userId: UserId): UserSettings {
        return userRepository.getSettings(userId) ?: throw RuntimeException("User not found")
    }

}
