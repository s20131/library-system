package pja.s20131.librarysystem

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import pja.s20131.librarysystem.infrastracture.ClockTestConfiguration
import pja.s20131.librarysystem.infrastracture.TestClock

@Component
@Import(ClockTestConfiguration::class)
@ActiveProfiles("test")
class BaseTestConfig {
    @Autowired
    protected lateinit var clock: TestClock
}
