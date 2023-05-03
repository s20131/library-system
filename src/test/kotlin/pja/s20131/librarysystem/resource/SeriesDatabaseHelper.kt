package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable.toSeries
import pja.s20131.librarysystem.domain.resource.model.Series

@Component
@Transactional
class SeriesDatabaseHelper {

    fun assertSeriesIsSaved(series: Series) {
        val retrievedSeries = SeriesTable
            .select { SeriesTable.id eq series.value }
            .singleOrNull()
            ?.toSeries()

        Assertions.assertThat(retrievedSeries).isEqualTo(retrievedSeries)
    }

    fun insertSeries(series: Series) =
        SeriesTable.insert {
            it[SeriesTable.id] = series.value
        }

}
