package pja.s20131.librarysystem.domain.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.user.UserRepository
import pja.s20131.librarysystem.infrastructure.exception.NotFoundException

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
