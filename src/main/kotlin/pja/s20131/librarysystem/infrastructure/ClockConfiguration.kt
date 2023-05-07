package pja.s20131.librarysystem.infrastructure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneId

@Configuration
class ClockConfiguration {

    @Bean
    fun clock(): Clock {
        return Clock.system(ZoneId.of("Europe/Warsaw"))
    }
}
