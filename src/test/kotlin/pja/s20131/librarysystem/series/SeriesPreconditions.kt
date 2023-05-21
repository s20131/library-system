package pja.s20131.librarysystem.series

import net.datafaker.Faker
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.domain.resource.model.Series

@Component
class SeriesPreconditions(
    private val seriesDatabaseHelper: SeriesDatabaseHelper,
) {
    private val faker = Faker()

    // random so tests don't fail if the same prop is picked
    fun exists(series: Series = Series(faker.familyGuy().location() + Math.random())): Series {
        return series.also { seriesDatabaseHelper.insertSeries(series) }
    }

}
