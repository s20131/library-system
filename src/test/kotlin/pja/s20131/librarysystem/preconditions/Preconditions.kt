package pja.s20131.librarysystem.preconditions

import org.springframework.stereotype.Component

@Component
class Preconditions(
    val user: UserPreconditions,
    val resource: ResourcePreconditions,
)
