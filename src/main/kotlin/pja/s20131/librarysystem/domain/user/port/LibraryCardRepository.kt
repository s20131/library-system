package pja.s20131.librarysystem.domain.user.port

import pja.s20131.librarysystem.domain.user.model.CardNumber
import pja.s20131.librarysystem.domain.user.model.LibraryCard
import pja.s20131.librarysystem.domain.user.model.UserId

interface LibraryCardRepository {
    fun getActive(userId: UserId): LibraryCard
    fun getActive(number: CardNumber): LibraryCard
    fun save(userId: UserId)
}