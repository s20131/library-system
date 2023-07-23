package pja.s20131.librarysystem.rental

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.IntegrationTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.adapter.database.resource.BookNotFoundException
import pja.s20131.librarysystem.adapter.database.user.LibraryCardDoesNotExistException
import pja.s20131.librarysystem.book.BookGen
import pja.s20131.librarysystem.domain.resource.InsufficientCopyAvailabilityException
import pja.s20131.librarysystem.domain.resource.LibraryNotMatchingException
import pja.s20131.librarysystem.domain.resource.RentalHistory
import pja.s20131.librarysystem.domain.resource.RentalService
import pja.s20131.librarysystem.domain.resource.RentalShortInfo
import pja.s20131.librarysystem.domain.resource.UserNotPermittedToAccessLibraryException
import pja.s20131.librarysystem.domain.resource.model.Availability
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.Penalty
import pja.s20131.librarysystem.domain.resource.model.RentalNotActiveException
import pja.s20131.librarysystem.domain.resource.model.RentalNotPaidOffException
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import pja.s20131.librarysystem.domain.resource.model.RentalPeriodNotOverlappingDatesException
import pja.s20131.librarysystem.domain.resource.model.RentalPeriodOverlappingDatesException
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import pja.s20131.librarysystem.domain.resource.model.RentalStatusTransition
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.resource.port.RentalNotFoundException
import pja.s20131.librarysystem.domain.user.model.CardNumber
import pja.s20131.librarysystem.domain.user.model.IsActive
import java.math.BigDecimal

@SpringBootTest
class RentalServiceTests @Autowired constructor(
    private val rentalService: RentalService,
    private val given: Preconditions,
    private val assert: Assertions,
) : IntegrationTestConfig() {

    @Test
    fun `should return user rentals`() {
        val user = given.user.exists().build()
        val (author, books) = given.author.exists().withBook().build()
        val library = given.library.exists().hasCopy(books[0].resourceId).build()
        val rental = given.rental.exists(user.userId, books[0].resourceId, library.libraryId, RentalPeriod.startRental(clock.now()))

        val response = rentalService.getUserRentals(user.userId)

        assertThat(response).containsExactly(
            RentalHistory(
                library.libraryId,
                books[0].toBasicData(),
                author.toBasicData(),
                rental.rentalPeriod.startDate,
                RentalStatus.ACTIVE,
                ResourceType.BOOK
            )
        )
    }

    @Test
    fun `should return user rentals sorted by finishing earliest`() {
        val user = given.user.exists().build()
        val (author, books, ebooks) = given.author.exists().withBook().withEbook().build()
        val library = given.library.exists().hasCopy(books[0].resourceId).hasCopy(ebooks[0].resourceId).build()
        val rental1 = given.rental.exists(user.userId, books[0].resourceId, library.libraryId, RentalPeriod.startRental(clock.now()))
        val rental2 = given.rental.exists(user.userId, ebooks[0].resourceId, library.libraryId, RentalPeriod.startRental(clock.yesterday()))

        val response = rentalService.getUserRentals(user.userId)

        assertThat(response).containsExactly(
            RentalHistory(
                library.libraryId,
                ebooks[0].toBasicData(),
                author.toBasicData(),
                rental2.rentalPeriod.startDate,
                RentalStatus.ACTIVE,
                ResourceType.EBOOK,
            ),
            RentalHistory(
                library.libraryId,
                books[0].toBasicData(),
                author.toBasicData(),
                rental1.rentalPeriod.startDate,
                RentalStatus.ACTIVE,
                ResourceType.BOOK,
            ),
        )
    }

    @Test
    fun `should get info about latest user rental of the same resource`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()))
        val expectedRental = given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.now()))
        given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.yesterday()))

        val response = rentalService.getLatestRentalShortInfo(book.resourceId, user.userId)

        assertThat(response).isEqualTo(
            RentalShortInfo(
                expectedRental.rentalStatus,
                expectedRental.rentalPeriod.finishTime,
                library.libraryName,
                penalty = null,
            )
        )
    }

    @Test
    fun `should throw exception when trying to get info about not existing rental`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]

        assertThrows<RentalNotFoundException> { rentalService.getLatestRentalShortInfo(book.resourceId, user.userId) }
    }

    @Test
    fun `should borrow a resource`() {
        val user = given.user.exists().build()
        val ebook = given.author.exists().withEbook().build().third[0]
        val library = given.library.exists().hasCopy(ebook.resourceId).build()

        rentalService.borrowResource(ebook.resourceId, library.libraryId, user.userId)

        assert.rental.isSaved(
            user.userId,
            ebook.resourceId,
            library.libraryId,
            RentalPeriod(clock.now(), clock.inDays(14)),
            RentalStatus.ACTIVE,
            penalty = null
        )
    }

    @Test
    fun `should throw exception when trying to borrow a resource with an active rental of this resource`() {
        val user = given.user.exists().build()
        val ebook = given.author.exists().withEbook().build().third[0]
        val library1 = given.library.exists().hasCopy(ebook.resourceId).build()
        val library2 = given.library.exists().hasCopy(ebook.resourceId).build()
        given.rental.exists(user.userId, ebook.resourceId, library1.libraryId, RentalPeriod.startRental(clock.lastWeek()))

        assertThrows<RentalPeriodOverlappingDatesException> { rentalService.borrowResource(ebook.resourceId, library2.libraryId, user.userId) }
    }

    @Test
    fun `should reserve a book for user to pickup after borrowing one`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()

        rentalService.borrowResource(book.resourceId, library.libraryId, user.userId)

        assert.rental.isSaved(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod(clock.now(), clock.inDays(2)),
            RentalStatus.RESERVED_TO_BORROW,
            penalty = null
        )
    }

    @Test
    fun `should throw exception when trying to borrow a resource with 0 copies in a library`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId, Availability(0)).build()

        assertThrows<InsufficientCopyAvailabilityException> {
            rentalService.borrowResource(book.resourceId, library.libraryId, user.userId)
        }
    }

    @Test
    fun `should let customer borrow a resource after making a valid reservation and availability is above 0`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId, Availability(1)).build()
        given.reservation.exists(user.userId, book.resourceId, library.libraryId)
        given.reservation.exists(given.user.exists().build().userId, book.resourceId, library.libraryId)
        given.reservation.exists(given.user.exists().build().userId, book.resourceId, library.libraryId)

        rentalService.borrowResource(book.resourceId, library.libraryId, user.userId)

        assert.rental.isSaved(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod(clock.now(), clock.inDays(2)),
            RentalStatus.RESERVED_TO_BORROW,
            penalty = null,
        )
    }

    @Test
    fun `should remove reservation after borrowing a resource`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId, Availability(1)).build()
        given.reservation.exists(user.userId, book.resourceId, library.libraryId)

        rentalService.borrowResource(book.resourceId, library.libraryId, user.userId)

        assert.reservation.isNotSaved(book.resourceId, user.userId)
    }

    @Test
    fun `should throw an error when trying to borrow a resource when there are too many active reservations`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        given.reservation.exists(given.user.exists().build().userId, book.resourceId, library.libraryId)
        given.reservation.exists(given.user.exists().build().userId, book.resourceId, library.libraryId)

        assertThrows<InsufficientCopyAvailabilityException> { rentalService.borrowResource(book.resourceId, library.libraryId, user.userId) }
    }

    @Test
    fun `should decrease availability by 1 after successful rental`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()

        rentalService.borrowResource(book.resourceId, library.libraryId, user.userId)

        assert.library.hasCopies(1, library.libraryId, book.resourceId)
    }

    @Test
    fun `should throw an error when trying to borrow a prolonged resource without paying it off before`() {
        val user = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        given.rental.exists(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod.startRental(clock.monthAgo()),
            RentalStatus.PROLONGED,
            Penalty(BigDecimal.TEN)
        )

        assertThrows<RentalNotPaidOffException> { rentalService.borrowResource(book.resourceId, library.libraryId, user.userId) }
    }

    @Test
    fun `should let librarian borrow a resource for customer`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val librarian = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasLibrarian(librarian.userId).hasCopy(book.resourceId).build()

        rentalService.borrowResourceForCustomer(library.libraryId, book.isbn, cardNumber, librarian.userId)

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId)
    }

    @Test
    fun `should throw an error when librarian tries to borrow a book for customer of library he doesn't work at`() {
        given.user.exists().hasCard(cardNumber).build()
        val librarian = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()

        assertThrows<UserNotPermittedToAccessLibraryException> {
            rentalService.borrowResourceForCustomer(
                library.libraryId,
                book.isbn,
                cardNumber,
                librarian.userId
            )
        }
    }

    @Test
    fun `should get customer's reserved to borrow books`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val book1 = given.author.exists().withBook().build().second[0]
        val book2 = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book1.resourceId).hasCopy(book2.resourceId).build()
        given.rental.exists(
            user.userId,
            book1.resourceId,
            library.libraryId,
            RentalPeriod.startReservationToBorrow(clock.yesterday()),
            RentalStatus.RESERVED_TO_BORROW
        )
        given.rental.exists(
            user.userId,
            book2.resourceId,
            library.libraryId,
            RentalPeriod.startReservationToBorrow(clock.yesterday()),
            RentalStatus.RESERVED_TO_BORROW
        )

        val response = rentalService.getCustomerAwaitingBooks(library.libraryId, cardNumber)

        assertThat(response).containsExactly(book1.toBookBasicData(), book2.toBookBasicData())
    }

    @Test
    fun `should get customer's reserved to borrow books no matter other stuff`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val book1 = given.author.exists().withBook().build().second[0]
        val book2 = given.author.exists().withBook().build().second[0]
        val irrelevantBook1 = given.author.exists().withBook().build().second[0]
        val irrelevantBook2 = given.author.exists().withBook().build().second[0]
        val library =
            given.library.exists().hasCopy(book1.resourceId).hasCopy(book2.resourceId).hasCopy(irrelevantBook1.resourceId).hasCopy(irrelevantBook2.resourceId)
                .build()
        given.rental.exists(
            user.userId,
            book1.resourceId,
            library.libraryId,
            RentalPeriod.startReservationToBorrow(clock.yesterday()),
            RentalStatus.RESERVED_TO_BORROW
        )
        given.rental.exists(
            user.userId,
            book2.resourceId,
            library.libraryId,
            RentalPeriod.startReservationToBorrow(clock.yesterday()),
            RentalStatus.RESERVED_TO_BORROW
        )
        given.rental.exists(user.userId, irrelevantBook2.resourceId, library.libraryId, RentalPeriod.startRental(clock.yesterday()), RentalStatus.ACTIVE)

        val response = rentalService.getCustomerAwaitingBooks(library.libraryId, cardNumber)

        assertThat(response).containsExactly(book1.toBookBasicData(), book2.toBookBasicData())
    }

    @Test
    fun `should throw an error when trying to get customer's awaiting books but he has an inactive card`() {
        val user = given.user.exists().hasCard(cardNumber, isActive = IsActive(false)).build()
        val book1 = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book1.resourceId).build()
        given.rental.exists(
            user.userId,
            book1.resourceId,
            library.libraryId,
            RentalPeriod.startReservationToBorrow(clock.yesterday()),
            RentalStatus.RESERVED_TO_BORROW
        )

        assertThrows<LibraryCardDoesNotExistException> { rentalService.getCustomerAwaitingBooks(library.libraryId, cardNumber) }
    }

    @Test
    fun `should complete book rental`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val librarian = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
        given.rental.exists(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod.startReservationToBorrow(clock.yesterday()),
            RentalStatus.RESERVED_TO_BORROW,
        )

        rentalService.changeRentalStatus(library.libraryId, book.isbn, RentalStatusTransition.START, cardNumber, librarian.userId)

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.now()), RentalStatus.ACTIVE, penalty = null)
    }

    @Test
    fun `should throw exception when trying to complete a book rental with not existing isbn`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val librarian = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
        given.rental.exists(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod.startReservationToBorrow(clock.yesterday()),
            RentalStatus.RESERVED_TO_BORROW
        )

        assertThrows<BookNotFoundException> {
            rentalService.changeRentalStatus(
                library.libraryId,
                ISBN("abc123"),
                RentalStatusTransition.START,
                cardNumber,
                librarian.userId
            )
        }
    }

    @Test
    fun `should throw exception when trying to complete rental after reservation expired`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val librarian = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
        given.rental.exists(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod.startReservationToBorrow(clock.lastWeek()),
            RentalStatus.RESERVED_TO_BORROW,
        )

        assertThrows<RentalPeriodNotOverlappingDatesException> {
            rentalService.changeRentalStatus(
                library.libraryId,
                book.isbn,
                RentalStatusTransition.START,
                cardNumber,
                librarian.userId
            )
        }
    }

    @Test
    fun `should throw an error when a librarian doesn't work at the requested library`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val librarian = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).build()
        given.rental.exists(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod.startReservationToBorrow(clock.lastWeek()),
            RentalStatus.RESERVED_TO_BORROW,
        )

        assertThrows<UserNotPermittedToAccessLibraryException> {
            rentalService.changeRentalStatus(
                library.libraryId,
                book.isbn,
                RentalStatusTransition.START,
                cardNumber,
                librarian.userId
            )
        }
    }

    @Nested
    inner class CheckBeforeReturningBook {
        @Test
        fun `should return a rental`() {
            val user = given.user.exists().hasCard(cardNumber).build()
            val librarian = given.user.exists().build()
            val book = given.author.exists().withBook().build().second[0]
            val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
            val rental = given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()))

            val response = rentalService.checkBeforeReturningBook(library.libraryId, book.isbn, cardNumber, librarian.userId)

            assertThat(response).isEqualTo(rental)
        }

        @Test
        fun `should validate if book exists`() {
            given.user.exists().hasCard(cardNumber).build()
            val book = BookGen.book()
            val librarian = given.user.exists().build()
            val library = given.library.exists().hasLibrarian(librarian.userId).build()

            assertThrows<BookNotFoundException> { rentalService.checkBeforeReturningBook(library.libraryId, book.isbn, cardNumber, librarian.userId) }
        }

        @Test
        fun `should validate if user's card is active`() {
            val user = given.user.exists().hasCard(cardNumber, isActive = IsActive(false)).build()
            val librarian = given.user.exists().build()
            val book = given.author.exists().withBook().build().second[0]
            val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
            given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()))

            assertThrows<LibraryCardDoesNotExistException> {
                rentalService.checkBeforeReturningBook(
                    library.libraryId,
                    book.isbn,
                    cardNumber,
                    librarian.userId
                )
            }
        }

        @Test
        fun `should validate if rental exists`() {
            given.user.exists().hasCard(cardNumber).build()
            val librarian = given.user.exists().build()
            val book = given.author.exists().withBook().build().second[0]
            val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()

            assertThrows<RentalNotFoundException> {
                rentalService.checkBeforeReturningBook(
                    library.libraryId,
                    book.isbn,
                    cardNumber,
                    librarian.userId
                )
            }
        }

        @Test
        fun `should validate if rental is in correct status`() {
            val user = given.user.exists().hasCard(cardNumber).build()
            val librarian = given.user.exists().build()
            val book = given.author.exists().withBook().build().second[0]
            val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
            given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()), RentalStatus.FINISHED)

            assertThrows<RentalNotActiveException> {
                rentalService.checkBeforeReturningBook(
                    library.libraryId,
                    book.isbn,
                    cardNumber,
                    librarian.userId
                )
            }
        }

        @Test
        fun `should validate if library matches the rental's library`() {
            val user = given.user.exists().hasCard(cardNumber).build()
            val librarian = given.user.exists().build()
            val book = given.author.exists().withBook().build().second[0]
            val library1 = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
            val library2 = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId, isSelected = false).build()
            given.rental.exists(user.userId, book.resourceId, library2.libraryId, RentalPeriod.startRental(clock.lastWeek()))

            assertThrows<LibraryNotMatchingException> {
                rentalService.checkBeforeReturningBook(
                    library1.libraryId,
                    book.isbn,
                    cardNumber,
                    librarian.userId
                )
            }
        }

        @Test
        fun `should validate if librarian works at the given library`() {
            val user = given.user.exists().hasCard(cardNumber).build()
            val librarian = given.user.exists().build()
            val book = given.author.exists().withBook().build().second[0]
            val library = given.library.exists().hasCopy(book.resourceId).build()
            given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()))

            assertThrows<UserNotPermittedToAccessLibraryException> {
                rentalService.checkBeforeReturningBook(
                    library.libraryId,
                    book.isbn,
                    cardNumber,
                    librarian.userId
                )
            }
        }
    }

    @Test
    fun `should successfully finish rental and increase availability`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val librarian = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
        val rental = given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()), RentalStatus.ACTIVE)

        rentalService.changeRentalStatus(library.libraryId, book.isbn, RentalStatusTransition.FINISH, cardNumber, librarian.userId)

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.FINISHED, penalty = null)
        assert.library.hasCopies(3, library.libraryId, book.resourceId)
    }

    @Test
    fun `should successfully cancel rental and increase availability`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val librarian = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
        val rental =
            given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()), RentalStatus.RESERVED_TO_BORROW)

        rentalService.changeRentalStatus(library.libraryId, book.isbn, RentalStatusTransition.CANCEL, cardNumber, librarian.userId)

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.CANCELLED, penalty = null)
        assert.library.hasCopies(3, library.libraryId, book.resourceId)
    }

    @Test
    fun `should successfully finish rental which was prolonged and increase availability`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val librarian = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
        val rental = given.rental.exists(
            user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()), RentalStatus.PROLONGED, Penalty(BigDecimal(10.00))
        )

        rentalService.changeRentalStatus(library.libraryId, book.isbn, RentalStatusTransition.PAY_OFF, cardNumber, librarian.userId)

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.FINISHED, Penalty(BigDecimal("10.00")))
        assert.library.hasCopies(3, library.libraryId, book.resourceId)
    }

    @Test
    fun `should successfully change rental status without increasing availability`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val librarian = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
        given.rental.exists(
            user.userId,
            book.resourceId,
            library.libraryId,
            RentalPeriod.startReservationToBorrow(clock.yesterday()),
            RentalStatus.RESERVED_TO_BORROW
        )

        rentalService.changeRentalStatus(library.libraryId, book.isbn, RentalStatusTransition.START, cardNumber, librarian.userId)

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.now()), RentalStatus.ACTIVE, penalty = null)
        assert.library.hasCopies(2, library.libraryId, book.resourceId)
    }

    @Test
    fun `should successfully change rental status without increasing availability and setting up penalty`() {
        val user = given.user.exists().hasCard(cardNumber).build()
        val librarian = given.user.exists().build()
        val book = given.author.exists().withBook().build().second[0]
        val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
        val rental = given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()), RentalStatus.ACTIVE)

        rentalService.changeRentalStatus(library.libraryId, book.isbn, RentalStatusTransition.PROLONG, cardNumber, librarian.userId)

        assert.rental.isSaved(user.userId, book.resourceId, library.libraryId, rental.rentalPeriod, RentalStatus.PROLONGED, Penalty(BigDecimal("2.50")))
    }

    @Nested
    inner class ReturnBook {
        @Test
        fun `should validate if book exists`() {
            given.user.exists().hasCard(cardNumber).build()
            val book = BookGen.book()
            val librarian = given.user.exists().build()
            val library = given.library.exists().hasLibrarian(librarian.userId).build()

            assertThrows<BookNotFoundException> {
                rentalService.changeRentalStatus(
                    library.libraryId,
                    book.isbn,
                    RentalStatusTransition.FINISH,
                    cardNumber,
                    librarian.userId
                )
            }
        }

        @Test
        fun `should validate if user's card is active`() {
            val user = given.user.exists().hasCard(cardNumber, isActive = IsActive(false)).build()
            val librarian = given.user.exists().build()
            val book = given.author.exists().withBook().build().second[0]
            val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
            given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()))

            assertThrows<LibraryCardDoesNotExistException> {
                rentalService.changeRentalStatus(
                    library.libraryId,
                    book.isbn,
                    RentalStatusTransition.FINISH,
                    cardNumber,
                    librarian.userId
                )
            }
        }

        @Test
        fun `should validate if rental exists`() {
            given.user.exists().hasCard(cardNumber).build()
            val librarian = given.user.exists().build()
            val book = given.author.exists().withBook().build().second[0]
            val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()

            assertThrows<RentalNotFoundException> {
                rentalService.changeRentalStatus(
                    library.libraryId,
                    book.isbn,
                    RentalStatusTransition.FINISH,
                    cardNumber,
                    librarian.userId
                )
            }
        }

        @Test
        fun `should validate if rental is in correct status`() {
            val user = given.user.exists().hasCard(cardNumber).build()
            val librarian = given.user.exists().build()
            val book = given.author.exists().withBook().build().second[0]
            val library = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
            given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()), RentalStatus.FINISHED)

            assertThrows<RentalNotActiveException> {
                rentalService.changeRentalStatus(
                    library.libraryId,
                    book.isbn,
                    RentalStatusTransition.FINISH,
                    cardNumber,
                    librarian.userId
                )
            }
        }

        @Test
        fun `should validate if library matches the rental's library`() {
            val user = given.user.exists().hasCard(cardNumber).build()
            val librarian = given.user.exists().build()
            val book = given.author.exists().withBook().build().second[0]
            val library1 = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId).build()
            val library2 = given.library.exists().hasCopy(book.resourceId).hasLibrarian(librarian.userId, isSelected = false).build()
            given.rental.exists(user.userId, book.resourceId, library2.libraryId, RentalPeriod.startRental(clock.lastWeek()))

            assertThrows<LibraryNotMatchingException> {
                rentalService.changeRentalStatus(
                    library1.libraryId,
                    book.isbn,
                    RentalStatusTransition.FINISH,
                    cardNumber,
                    librarian.userId
                )
            }
        }

        @Test
        fun `should validate if librarian works at the given library`() {
            val user = given.user.exists().hasCard(cardNumber).build()
            val librarian = given.user.exists().build()
            val book = given.author.exists().withBook().build().second[0]
            val library = given.library.exists().hasCopy(book.resourceId).build()
            given.rental.exists(user.userId, book.resourceId, library.libraryId, RentalPeriod.startRental(clock.lastWeek()))

            assertThrows<UserNotPermittedToAccessLibraryException> {
                rentalService.changeRentalStatus(
                    library.libraryId,
                    book.isbn,
                    RentalStatusTransition.FINISH,
                    cardNumber,
                    librarian.userId
                )
            }
        }
    }

    companion object {
        val cardNumber = CardNumber(1024)
    }
}
