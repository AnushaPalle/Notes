package com.shopflow.monolith;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import java.util.List;
import java.util.stream.Stream;

/** Executable architecture: boundaries described in ADR 0002 fail the build when violated. */
@AnalyzeClasses(packages = "com.shopflow.monolith", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final List<String> MODULES = List.of("users", "catalog", "orders", "billing");
    private static final String BASE = "com.shopflow.monolith.";

    @ArchTest
    static final ArchRule domainIsIndependentOfOuterLayers = noClasses().that().resideInAPackage("com.shopflow.monolith..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("com.shopflow.monolith..web..", "com.shopflow.monolith..application..", "com.shopflow.monolith..infra..");

    @ArchTest
    static final ArchRule applicationDoesNotDependOnWeb = noClasses().that().resideInAPackage("com.shopflow.monolith..application..")
            .should().dependOnClassesThat().resideInAPackage("com.shopflow.monolith..web..");

    @ArchTest
    static void modulesTalkToEachOtherOnlyThroughContracts(JavaClasses classes) {
        for (String module : MODULES) {
            String[] otherInternals = MODULES.stream()
                    .filter(other -> !other.equals(module))
                    .flatMap(other -> Stream.of("web", "application", "domain", "infra")
                            .map(layer -> BASE + other + "." + layer + ".."))
                    .toArray(String[]::new);
            noClasses().that().resideInAPackage(BASE + module + "..")
                    .should().dependOnClassesThat().resideInAnyPackage(otherInternals)
                    .check(classes);
        }
    }
}
