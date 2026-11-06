package com.elevatementoring.backend.architecture;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.elevatementoring.archfixtures.recognition.application.HoursService;
import com.elevatementoring.archfixtures.scheduling.application.SessionQueries;
import com.elevatementoring.archfixtures.scheduling.domain.JpaSlot;
import com.elevatementoring.archfixtures.scheduling.domain.Session;
import com.elevatementoring.archfixtures.scheduling.domain.SessionCompleted;
import com.elevatementoring.archfixtures.scheduling.domain.SpringAwareSlot;
import com.elevatementoring.archfixtures.scheduling.web.SessionController;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

/**
 * Proves the rules in ArchitectureTest actually catch violations, using the fixture classes in
 * com.elevatementoring.archfixtures (outside the app package, so Spring and Hibernate never scan them;
 * each one marked "Violation" breaks exactly one rule on purpose).
 */
class ArchitectureRulesTest {

	private static final String FIXTURES = "com.elevatementoring.archfixtures";

	private static final JavaClasses fixtures = new ClassFileImporter().importPackages(FIXTURES);

	@Test
	void domainIsFrameworkFreeRejectsSpringAndJpa() {
		assertThatThrownBy(() -> ArchitectureRules.domainIsFrameworkFree(FIXTURES).check(fixtures))
				.hasMessageContaining(SpringAwareSlot.class.getName())
				.hasMessageContaining(JpaSlot.class.getName());
	}

	@Test
	void applicationDoesNotDependOnAdaptersRejectsInfrastructure() {
		assertThatThrownBy(() -> ArchitectureRules.applicationDoesNotDependOnAdapters(FIXTURES).check(fixtures))
				.hasMessageContaining(SessionQueries.class.getName());
	}

	@Test
	void webDoesNotAccessInfrastructureRejectsRepositoryInController() {
		assertThatThrownBy(() -> ArchitectureRules.webDoesNotAccessInfrastructure(FIXTURES).check(fixtures))
				.hasMessageContaining(SessionController.class.getName());
	}

	@Test
	void modulesTalkOnlyThroughApplicationOrEventsRejectsOtherModulesDomain() {
		assertThatThrownBy(() -> ArchitectureRules.modulesTalkOnlyThroughApplicationOrEvents(FIXTURES).check(fixtures))
				.hasMessageContaining(HoursService.class.getName() + ".session> has type <" + Session.class.getName() + ">")
				.hasMessageNotContaining("<" + SessionCompleted.class.getName() + ">")
				.hasMessageNotContaining("<" + SessionQueries.class.getName() + ">");
	}

}
