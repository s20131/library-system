package pja.s20131.librarysystem

import org.springframework.stereotype.Component
import pja.s20131.librarysystem.library.LibraryPreconditions
import pja.s20131.librarysystem.resource.AuthorPreconditions
import pja.s20131.librarysystem.resource.SeriesPreconditions
import pja.s20131.librarysystem.user.UserPreconditions

@Component
class Preconditions(
    val user: UserPreconditions,
    val author: AuthorPreconditions,
    val library: LibraryPreconditions,
    val series: SeriesPreconditions,
)
