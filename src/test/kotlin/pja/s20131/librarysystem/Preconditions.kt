package pja.s20131.librarysystem

import org.springframework.stereotype.Component
import pja.s20131.librarysystem.library.LibraryPreconditions
import pja.s20131.librarysystem.author.AuthorPreconditions
import pja.s20131.librarysystem.rental.RentalPreconditions
import pja.s20131.librarysystem.reservation.ReservationPreconditions
import pja.s20131.librarysystem.series.SeriesPreconditions
import pja.s20131.librarysystem.user.UserPreconditions

@Component
class Preconditions(
    val user: UserPreconditions,
    val author: AuthorPreconditions,
    val library: LibraryPreconditions,
    val series: SeriesPreconditions,
    val rental: RentalPreconditions,
    val reservation: ReservationPreconditions,
)
