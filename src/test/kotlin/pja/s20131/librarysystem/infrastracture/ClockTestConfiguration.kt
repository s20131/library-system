package pja.s20131.librarysystem.infrastracture

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@TestConfiguration
class ClockTestConfiguration {

    @Bean
    @Primary
    fun testClock(): TestClock {
        return TestClock()
    }
}

class TestClock : Clock() {
    private val zone = ZoneId.of("Europe/Warsaw")
    private val fixedClock = fixed(Instant.parse("2023-05-07T17:55:00.000Z"), zone)

    override fun instant(): Instant {
        return fixedClock.instant()
    }

    override fun withZone(zone: ZoneId?): Clock {
        return fixedClock.withZone(zone)
    }

    override fun getZone(): ZoneId {
        return fixedClock.zone
    }

    fun now(): Instant {
        return instant()
    }

    fun yesterday(): Instant {
        return instant().minus(1, ChronoUnit.DAYS)
    }

    fun lastWeek(): Instant {
        return instant().minus(7, ChronoUnit.DAYS)
    }

    fun inDays(daysToAdd: Long): Instant {
        return instant().plus(daysToAdd, ChronoUnit.DAYS)
    }
}
