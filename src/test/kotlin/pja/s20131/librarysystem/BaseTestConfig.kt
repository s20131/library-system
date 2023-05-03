package pja.s20131.librarysystem

import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import pja.s20131.librarysystem.adapter.database.library.LibraryTable
import pja.s20131.librarysystem.adapter.database.resource.AuthorTable
import pja.s20131.librarysystem.adapter.database.resource.BookTable
import pja.s20131.librarysystem.adapter.database.resource.EbookTable
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable
import pja.s20131.librarysystem.adapter.database.resource.StorageTable
import pja.s20131.librarysystem.adapter.database.user.UserSettingsTable
import pja.s20131.librarysystem.adapter.database.user.UserTable

open class BaseTestConfig {

    @BeforeEach
    fun clear() {
        transaction {
            StorageTable.deleteAll()
            BookTable.deleteAll()
            EbookTable.deleteAll()
            ResourceTable.deleteAll()
            AuthorTable.deleteAll()
            SeriesTable.deleteAll()
            LibraryTable.deleteAll()
            UserSettingsTable.deleteAll()
            UserTable.deleteAll()
        }
    }

}
