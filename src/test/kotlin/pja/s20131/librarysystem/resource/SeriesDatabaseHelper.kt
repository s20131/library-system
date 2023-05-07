package pja.s20131.librarysystem.resource

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

    fun findBy(series: Series): Series? {
        return SeriesTable
            .select { SeriesTable.id eq series.value }
            .singleOrNull()
            ?.toSeries()
    }

    fun insertSeries(series: Series) =
        SeriesTable.insert {
            it[SeriesTable.id] = series.value
        }

    fun insertSeriesWithoutConflict(series: Series) {
        val result = SeriesTable.select { SeriesTable.id eq series.value }.singleOrNull()
        result ?: insertSeries(series)
    }

}
