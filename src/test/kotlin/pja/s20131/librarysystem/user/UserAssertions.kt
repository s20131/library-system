package pja.s20131.librarysystem.user

import org.assertj.core.api.Assertions.assertThat
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.user.model.UserId

@Component
class UserAssertions(
    private val userDatabaseHelper: UserDatabaseHelper,
) {
    fun isSaved(userId: UserId) {
        val user = userDatabaseHelper.findBy(userId)
        val settings = userDatabaseHelper.findSettingsBy(userId)

        assertThat(user).isNotNull
        assertThat(user!!.userId).isEqualTo(userId)
        assertThat(settings).isNotNull
    }
}