package pja.s20131.librarysystem.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Service

class ArchitectureTests {

    @Test
    fun `services should be in service packages`() {
        classes()
            .that().areAnnotatedWith(Service::class.java)
            .should().haveNameMatching(".*Service")
            .andShould().resideInAPackage("..service..")
    }

}
