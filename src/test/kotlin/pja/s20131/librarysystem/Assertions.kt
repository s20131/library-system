package pja.s20131.librarysystem

import org.springframework.stereotype.Component
import pja.s20131.librarysystem.author.AuthorAssertions
import pja.s20131.librarysystem.resource.BookAssertions
import pja.s20131.librarysystem.resource.EbookAssertions
import pja.s20131.librarysystem.resource.ResourceAssertions
import pja.s20131.librarysystem.series.SeriesAssertions
import pja.s20131.librarysystem.user.UserAssertions
import pja.s20131.librarysystem.rental.RentalAssertions
import pja.s20131.librarysystem.reservation.ReservationAssertions

@Component
class Assertions(
    val user: UserAssertions,
    val resource: ResourceAssertions,
    val book: BookAssertions,
    val ebook: EbookAssertions,
    val author: AuthorAssertions,
    val series: SeriesAssertions,
    val rental: RentalAssertions,
    val reservation: ReservationAssertions,
)
