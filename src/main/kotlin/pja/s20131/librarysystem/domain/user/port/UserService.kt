package pja.s20131.librarysystem.domain.user.port

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.user.model.UserBasicData
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.NotFoundException

@Service
@Transactional
class UserService(
    val userRepository: UserRepository
) {

    fun getUser(userId: UserId): UserBasicData {
        return userRepository.get(userId) ?: throw UserNotFoundException(userId)
    }

    fun getUserSettings(userId: UserId): UserSettings {
        return userRepository.getSettings(userId) ?: throw UserNotFoundException(userId)
    }

}

class UserNotFoundException(id: UserId) : NotFoundException("User with id=${id.value} not found")
