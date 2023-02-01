package pja.s20131.librarysystem.domain.user

data class UserSettings(
    val sendEndOfRentalReminder: SendEndOfRentalReminder,
    val sendWhenAvailableReminder: SendWhenAvailableReminder,
    val kindleEmail: KindleEmail?,
)

@JvmInline
value class SendEndOfRentalReminder(val raw: Boolean)

@JvmInline
value class SendWhenAvailableReminder(val raw: Boolean)

@JvmInline
value class KindleEmail(val raw: String)
