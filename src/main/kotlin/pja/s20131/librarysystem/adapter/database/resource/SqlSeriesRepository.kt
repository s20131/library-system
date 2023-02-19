package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.exposed.TextIdTable
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.port.SeriesRepository

@Repository
class SqlSeriesRepository : SeriesRepository {
    override fun getAll(): List<Series> {
        return SeriesTable
            .selectAll()
            .map { it.toSeries() }
    }

    override fun insert(series: Series) {
        SeriesTable.insert {
            it[SeriesTable.id] = series.value
        }
    }
}

private fun ResultRow.toSeries() = Series(this[SeriesTable.id].value)

object SeriesTable : TextIdTable("series", "name")
