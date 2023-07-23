package pja.s20131.librarysystem

import org.junit.jupiter.api.BeforeAll
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgisContainerProvider
import org.testcontainers.utility.MountableFile

abstract class IntegrationTestConfig : BaseTestConfig() {

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
