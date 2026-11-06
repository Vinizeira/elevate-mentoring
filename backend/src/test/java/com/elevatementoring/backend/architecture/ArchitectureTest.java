package com.elevatementoring.backend.architecture;

import com.elevatementoring.backend.BackendApplication;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = BackendApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

	private static final String BASE = BackendApplication.class.getPackageName();

	@ArchTest
	static final ArchRule domainIsFrameworkFree = ArchitectureRules.domainIsFrameworkFree(BASE);

	@ArchTest
	static final ArchRule applicationDoesNotDependOnAdapters = ArchitectureRules.applicationDoesNotDependOnAdapters(BASE);

	@ArchTest
	static final ArchRule webDoesNotAccessInfrastructure = ArchitectureRules.webDoesNotAccessInfrastructure(BASE);

	@ArchTest
	static final ArchRule modulesTalkOnlyThroughApplicationOrEvents = ArchitectureRules.modulesTalkOnlyThroughApplicationOrEvents(BASE);

}
