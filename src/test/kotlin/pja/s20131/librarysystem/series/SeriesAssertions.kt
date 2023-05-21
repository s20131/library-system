package pja.s20131.librarysystem.series

import org.assertj.core.api.Assertions.assertThat
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.resource.model.Series

@Component
class SeriesAssertions(
    private val seriesDatabaseHelper: SeriesDatabaseHelper,
) {
    fun isSaved(series: Series) {
        val retrievedSeries = seriesDatabaseHelper.findBy(series)

        assertThat(retrievedSeries).isEqualTo(retrievedSeries)
    }
}
