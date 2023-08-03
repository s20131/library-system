package pja.s20131.librarysystem.rental

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.domain.resource.model.RentalPeriod
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.stream.Stream


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RentalPeriodTests : BaseTestConfig() {

    @ParameterizedTest
    @MethodSource("overlappingPeriods")
    fun `should return periods are overlapped`(rentalPeriod1: RentalPeriod, rentalPeriod2: RentalPeriod) {
        assertThat(rentalPeriod1.isOverlapped(rentalPeriod2)).isTrue
        assertThat(rentalPeriod2.isOverlapped(rentalPeriod1)).isTrue
    }

    @ParameterizedTest
    @MethodSource("notOverlappingPeriods")
    fun `should return periods are not overlapped`(rentalPeriod1: RentalPeriod, rentalPeriod2: RentalPeriod) {
        assertThat(rentalPeriod1.isOverlapped(rentalPeriod2)).isFalse
        assertThat(rentalPeriod2.isOverlapped(rentalPeriod1)).isFalse
    }

    fun overlappingPeriods(): Stream<Arguments> = Stream.of(
        Arguments.of(
            RentalPeriod(YESTERDAY, NOW), RentalPeriod(LAST_WEEK, IN_2_DAYS),
            RentalPeriod(NOW, IN_2_DAYS), RentalPeriod(MONTH_AGO, NOW),
        )
    )

    fun notOverlappingPeriods(): Stream<Arguments> = Stream.of(
        Arguments.of(
            RentalPeriod(YESTERDAY, NOW), RentalPeriod(IN_1_MILLI, IN_2_DAYS),
        )
    )

    companion object {
        val MONTH_AGO: Instant = Instant.now().minus(30, ChronoUnit.DAYS)
        val LAST_WEEK: Instant = Instant.now().minus(7, ChronoUnit.DAYS)
        val YESTERDAY: Instant = Instant.now().minus(1, ChronoUnit.DAYS)
        val NOW: Instant = Instant.now()
        val IN_2_DAYS: Instant = Instant.now().plus(2, ChronoUnit.DAYS)
        val IN_1_MILLI: Instant = Instant.now().plus(1, ChronoUnit.MILLIS)
    }
}