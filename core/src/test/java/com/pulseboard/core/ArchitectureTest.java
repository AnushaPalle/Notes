package com.pulseboard.core;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.pulseboard.core", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainIsPure = noClasses().that().resideInAPackage("com.pulseboard.core.domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.pulseboard.core.application..", "com.pulseboard.core.adapter..", "org.springframework..");

    @ArchTest
    static final ArchRule applicationDoesNotKnowAdapters = noClasses()
            .that().resideInAPackage("com.pulseboard.core.application..")
            .should().dependOnClassesThat().resideInAPackage("com.pulseboard.core.adapter..");

    @ArchTest
    static final ArchRule inboundAdaptersDoNotTouchOutboundAdapters = noClasses()
            .that().resideInAPackage("com.pulseboard.core.adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("com.pulseboard.core.adapter.out..");
}
