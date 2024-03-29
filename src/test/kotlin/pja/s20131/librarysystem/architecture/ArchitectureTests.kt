package pja.s20131.librarysystem.architecture

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.properties.HasName
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

@AnalyzeClasses(packages = ["pja.s20131.librarysystem"], importOptions = [ImportOption.DoNotIncludeTests::class])
class ArchitectureTests {

    // tests as variables, because in case of methods needs an additional "check()" call in every invoke
    @ArchTest
    val hexagonalArchitectureLayerCheck: ArchRule =
        onionArchitecture()
            .domainModels("..domain..model..")
            .domainServices("..domain..")
            .applicationServices("..infrastructure..")
            .adapter("api", "..adapter.api..")
            .adapter("database", "..adapter.database..")
            .ignoreDependency(HasName.Predicates.nameMatching(".*ExceptionHandler"), DescribedPredicate.alwaysTrue())

    @ArchTest
    val controllersShouldBeInAdapterApiPackages: ArchRule =
        classes()
            .that().areAnnotatedWith(RestController::class.java)
            .should().haveNameMatching(".*Endpoints")
            .andShould().resideInAPackage("..adapter.api..")

    @ArchTest
    val repositoriesShouldBeInAdapterDatabasePackages: ArchRule =
        classes()
            .that().areAnnotatedWith(Repository::class.java)
            .should().haveNameMatching(".*Repository")
            .andShould().resideInAPackage("..adapter.database..")

    @ArchTest
    val servicesShouldBeInDomainPackages: ArchRule =
        classes()
            .that().areAnnotatedWith(Service::class.java)
            .should().haveNameMatching(".*Service")
            .andShould().resideInAPackage("..domain..")

    @ArchTest
    val domainShouldBeIndependentFromAdapter: ArchRule =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..adapter..")

    @ArchTest
    val apiAdapterShouldHaveNoConnectionWithDatabase: ArchRule =
        noClasses()
            .that().resideInAPackage("..adapter.api..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.database..")

    @ArchTest
    val databaseAdapterShouldHaveNoConnectionWithApi: ArchRule =
        noClasses()
            .that().resideInAPackage("..adapter.database..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.api..")

}
