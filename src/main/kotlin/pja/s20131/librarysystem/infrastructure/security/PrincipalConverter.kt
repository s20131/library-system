package pja.s20131.librarysystem.infrastructure.security

import java.security.Principal
import java.util.UUID
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.user.model.UserId

@Component
class PrincipalConverter : Converter<Principal, UserId> {

    override fun convert(source: Principal): UserId {
        return UserId(UUID.fromString(source.name))
    }

}
