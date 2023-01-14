package pja.s20131.librarysystem.infrastructure.database

import javax.sql.DataSource
import org.jetbrains.exposed.spring.SpringTransactionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfiguration(private val dataSource: DataSource) {

    @Bean
    fun transactionManager() = SpringTransactionManager(dataSource)
}
