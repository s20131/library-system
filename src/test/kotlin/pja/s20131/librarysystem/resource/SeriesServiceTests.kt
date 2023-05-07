package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.assertions.Assertions
import pja.s20131.librarysystem.domain.resource.SeriesService
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.preconditions.Preconditions

@SpringBootTest
class SeriesServiceTests @Autowired constructor(
    private val seriesService: SeriesService,
    private val preconditions: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should get all series`() {
        val series1 = preconditions.resource.seriesExists()
        val series2 = preconditions.resource.seriesExists()

        val response = seriesService.getAllSeries()

        assertThat(response).containsExactly(series1, series2)
    }

    @Test
    fun `should correctly insert series`() {
        val series = Series("biografia")
        seriesService.addSeries(series)

        assert.series.isSaved(series)
    }

}
