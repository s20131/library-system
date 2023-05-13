package pja.s20131.librarysystem.resource

import net.datafaker.Faker
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.resource.model.Series

@Component
class SeriesPreconditions(
    private val seriesDatabaseHelper: SeriesDatabaseHelper,
) {
    private val faker = Faker()


    fun exists(series: Series = Series(faker.familyGuy().location())): Series {
        return series.also { seriesDatabaseHelper.insertSeries(series) }
    }

}
