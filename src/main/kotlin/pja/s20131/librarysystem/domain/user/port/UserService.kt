package pja.s20131.librarysystem.domain.user.port

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.user.model.UserBasicData
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings

@Service
@Transactional
class UserService(
    val userRepository: UserRepository,
) {

    fun getUser(userId: UserId): UserBasicData {
        return userRepository.getBy(userId).toBasicData()
    }

    fun getUserSettings(userId: UserId): UserSettings {
        return userRepository.getSettings(userId)
    }

}
