package pja.s20131.librarysystem.series

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.domain.resource.SeriesService
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.Preconditions

@SpringBootTest
class SeriesServiceTests @Autowired constructor(
    private val seriesService: SeriesService,
    private val given: Preconditions,
    private val assert: Assertions,
) : BaseTestConfig() {

    @Test
    fun `should get all series`() {
        val series1 = given.series.exists()
        val series2 = given.series.exists()

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
