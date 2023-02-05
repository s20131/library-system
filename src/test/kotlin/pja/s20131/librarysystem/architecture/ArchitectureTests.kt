package pja.s20131.librarysystem.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

@AnalyzeClasses(packages = ["pja.s20131.librarysystem"], importOptions = [ImportOption.DoNotIncludeTests::class])
class ArchitectureTests {

    @ArchTest
    fun `hexagonal architecture layer check`(classes: JavaClasses) {
        onionArchitecture()
            .domainModels("..domain..model..")
            .domainServices("..domain..port..")
            .applicationServices("..infrastructure..")
            .adapter("api", "..adapter.api..")
            .adapter("database", "..adapter.database..")
            .check(classes)
    }

    @ArchTest
    fun `controllers should be in adapter-api packages`(classes: JavaClasses) {
        classes()
            .that().areAnnotatedWith(RestController::class.java)
            .should().haveNameMatching(".*Endpoints")
            .andShould().resideInAPackage("..adapter.api..")
            .check(classes)
    }

    @ArchTest
    fun `repositories should be in adapter-database packages`(classes: JavaClasses) {
        classes()
            .that().areAnnotatedWith(Repository::class.java)
            .should().haveNameMatching(".*Repository")
            .andShould().resideInAPackage("..adapter.database..")
            .check(classes)
    }

    @ArchTest
    fun `services should be in domain-port packages`(classes: JavaClasses) {
        classes()
            .that().areAnnotatedWith(Service::class.java)
            .should().haveNameMatching(".*Service")
            .andShould().resideInAPackage("..domain..port..")
            .check(classes)
    }

    @ArchTest
    fun `domain should be independent from adapter`(classes: JavaClasses) {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..adapter..")
            .check(classes)
    }

}
