package pja.s20131.librarysystem

import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgisContainerProvider
import org.testcontainers.utility.MountableFile
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

abstract class IntegrationTestConfig : BaseTestConfig() {

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

    companion object {
        @JvmStatic
        val postgis: JdbcDatabaseContainer<*> = PostgisContainerProvider()
            .newInstance("latest")
            .withUsername("test")
            .withDatabaseName("test")
            .withUrlParam("stringtype", "unspecified")
            .also {
                it.withCopyFileToContainer(MountableFile.forClasspathResource("sql/01_schema.sql"), "/docker-entrypoint-initdb.d/1-schema.sql")
                it.withCopyFileToContainer(MountableFile.forClasspathResource("sql/02_db_objects_pre.sql"), "/docker-entrypoint-initdb.d/2-db-objects.sql")
                it.withCopyFileToContainer(MountableFile.forClasspathResource("sql/mock_time.sql"), "/docker-entrypoint-initdb.d/5-mock-time.sql")
            }

        @DynamicPropertySource
        @JvmStatic
        fun postgresqlProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgis.jdbcUrl }
            registry.add("spring.datasource.username") { postgis.username }
            registry.add("spring.datasource.password") { postgis.password }
        }

        @JvmStatic
        @BeforeAll
        fun start() {
            postgis.start()
            postgis.withUsername("spring_app")
        }
    }
}
