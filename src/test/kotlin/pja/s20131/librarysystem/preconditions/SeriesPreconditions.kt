package pja.s20131.librarysystem.preconditions

import net.datafaker.Faker
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.resource.SeriesDatabaseHelper

@Component
class SeriesPreconditions(
    private val seriesDatabaseHelper: SeriesDatabaseHelper,
) {
    private val faker = Faker()


    fun exists(series: Series = Series(faker.familyGuy().location())): Series {
        return series.also { seriesDatabaseHelper.insertSeries(series) }
    }

}
