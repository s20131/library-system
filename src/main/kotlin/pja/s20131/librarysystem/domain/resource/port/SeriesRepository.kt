package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.model.Series

interface SeriesRepository {
    fun getAll(): List<Series>
    fun insert(series: Series)
}
