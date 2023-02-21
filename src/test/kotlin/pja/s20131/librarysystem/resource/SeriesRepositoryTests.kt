package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.port.SeriesRepository
import java.sql.SQLException

@SpringBootTest
@Transactional
class SeriesRepositoryTests @Autowired constructor(
    val seriesRepository: SeriesRepository,
    val resourceDatabaseHelper: ResourceDatabaseHelper,
) {
    @BeforeEach
    fun clear() {
        SeriesTable.deleteAll()
    }

    @Test
    fun `should correctly insert a series`() {
        val series = Series("biografia")
        seriesRepository.insert(series)

        val allSeries = seriesRepository.getAll()
        assertThat(allSeries).containsOnly(series)
    }

    @Test
    fun `should return an SQL exception when trying to add 2 identical series`() {
        val series = Series("biografia")
        resourceDatabaseHelper.insertSeries(series)

        assertThatThrownBy {
            seriesRepository.insert(series)
        }.isInstanceOf(SQLException::class.java)
    }
}
