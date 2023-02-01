package pja.s20131.librarysystem.api.user

import pja.s20131.librarysystem.domain.user.Email
import pja.s20131.librarysystem.domain.user.FirstName
import pja.s20131.librarysystem.domain.user.KindleEmail
import pja.s20131.librarysystem.domain.user.LastName
import pja.s20131.librarysystem.domain.user.SendEndOfRentalReminder
import pja.s20131.librarysystem.domain.user.SendWhenAvailableReminder

data class GetUserResponse(
    val firstName: FirstName,
    val lastName: LastName,
    val email: Email,
)

data class GetUserSettingsResponse(
    val sendEndOfRentalReminder: SendEndOfRentalReminder,
    val sendWhenAvailableReminder: SendWhenAvailableReminder,
    val kindleEmail: KindleEmail?,
)
