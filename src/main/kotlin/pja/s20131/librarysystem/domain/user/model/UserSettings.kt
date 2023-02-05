package pja.s20131.librarysystem.domain.user.model

data class UserSettings(
    val sendEndOfRentalReminder: SendEndOfRentalReminder,
    val sendWhenAvailableReminder: SendWhenAvailableReminder,
    val kindleEmail: KindleEmail?,
)

@JvmInline
value class SendEndOfRentalReminder(val value: Boolean)

@JvmInline
value class SendWhenAvailableReminder(val value: Boolean)

@JvmInline
value class KindleEmail(val value: String)
