package pja.s20131.librarysystem

import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.adapter.database.library.LibrarianTable
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.adapter.database.resource.AuthorTable
import pja.s20131.librarysystem.adapter.database.resource.BookTable
import pja.s20131.librarysystem.adapter.database.resource.CopyTable
import pja.s20131.librarysystem.adapter.database.resource.CoverTable
import pja.s20131.librarysystem.adapter.database.resource.EbookTable
import pja.s20131.librarysystem.adapter.database.resource.RentalTable
import pja.s20131.librarysystem.adapter.database.resource.ReservationTable
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable
import pja.s20131.librarysystem.adapter.database.resource.StorageTable
import pja.s20131.librarysystem.adapter.database.user.LibraryCardTable
import pja.s20131.librarysystem.adapter.database.user.UserSettingsTable
import pja.s20131.librarysystem.adapter.database.user.UserTable
import pja.s20131.librarysystem.infrastracture.ClockTestConfiguration
import pja.s20131.librarysystem.infrastracture.TestClock

@Component
@Import(ClockTestConfiguration::class)
class BaseTestConfig {

    @Autowired
    protected lateinit var clock: TestClock

    @AfterEach
    fun clear() {
        transaction {
            StorageTable.deleteAll()
            RentalTable.deleteAll()
            ReservationTable.deleteAll()
            CopyTable.deleteAll()
            CoverTable.deleteAll()
            BookTable.deleteAll()
            EbookTable.deleteAll()
            ResourceTable.deleteAll()
            AuthorTable.deleteAll()
            SeriesTable.deleteAll()
            LibrarianTable.deleteAll()
            LibraryTable.deleteAll()
            LibraryCardTable.deleteAll()
            UserSettingsTable.deleteAll()
            UserTable.deleteAll()
        }
    }

}
