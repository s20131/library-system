package pja.s20131.librarysystem.domain.resource.port

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.resource.model.Series

@Service
@Transactional
class SeriesService(
    val seriesRepository: SeriesRepository,
) {
    fun getAllSeries(): List<Series> {
        return seriesRepository.getAll()
    }

    fun addSeries(series: Series) {
        seriesRepository.insert(series)
    }
}
