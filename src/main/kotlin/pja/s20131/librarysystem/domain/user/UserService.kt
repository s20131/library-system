package pja.s20131.librarysystem.domain.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.library.port.LibrarianRepository
import pja.s20131.librarysystem.domain.user.model.CardNumber
import pja.s20131.librarysystem.domain.user.model.LibraryCard
import pja.s20131.librarysystem.domain.user.model.UserBasicData
import pja.s20131.librarysystem.domain.user.model.UserId
import pja.s20131.librarysystem.domain.user.model.UserSettings
import pja.s20131.librarysystem.domain.user.port.LibraryCardRepository
import pja.s20131.librarysystem.domain.user.port.UserRepository

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val libraryCardRepository: LibraryCardRepository,
) {

    fun getUser(userId: UserId): UserBasicData {
        return userRepository.getBy(userId).toBasicData()
    }

    fun getUser(cardNumber: CardNumber): UserBasicData {
        return userRepository.getBy(cardNumber).toBasicData()
    }

    fun getUserSettings(userId: UserId): UserSettings {
        return userRepository.getSettingsBy(userId)
    }

    fun getUserLibraryCard(userId: UserId): LibraryCard {
        return libraryCardRepository.getActive(userId)
    }
}
