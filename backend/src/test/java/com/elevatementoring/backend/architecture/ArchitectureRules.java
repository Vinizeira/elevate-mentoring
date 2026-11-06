package com.elevatementoring.backend.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import java.util.Optional;

import com.elevatementoring.backend.shared.domain.DomainEvent;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

/**
 * Layer and module rules from ADR-001 and ADR-002. Each module lives in {@code <base>.<module>}
 * with the layers domain, application, infrastructure and web; {@code <base>.shared} is the shared kernel.
 * The rules take the base package so they can also be checked against the fixtures in ArchitectureRulesTest.
 */
final class ArchitectureRules {

	static final String SHARED_KERNEL = "shared";

	private ArchitectureRules() {
	}

	static ArchRule domainIsFrameworkFree(String basePackage) {
		return noClasses().that().resideInAPackage(basePackage + "..domain..")
				.should().dependOnClassesThat().resideInAnyPackage(
						"org.springframework..", "jakarta.persistence..", "org.hibernate..",
						basePackage + "..application..", basePackage + "..infrastructure..", basePackage + "..web..")
				.because("the domain is plain Java: no Spring, no JPA, and it does not know the outer layers")
				.allowEmptyShould(true);
	}

	static ArchRule applicationDoesNotDependOnAdapters(String basePackage) {
		return noClasses().that().resideInAPackage(basePackage + "..application..")
				.should().dependOnClassesThat().resideInAnyPackage(basePackage + "..infrastructure..", basePackage + "..web..")
				.because("use cases talk to infrastructure through ports (interfaces) they own")
				.allowEmptyShould(true);
	}

	static ArchRule webDoesNotAccessInfrastructure(String basePackage) {
		return noClasses().that().resideInAPackage(basePackage + "..web..")
				.should().dependOnClassesThat().resideInAPackage(basePackage + "..infrastructure..")
				.because("controllers call use cases in application, never repositories or clients directly")
				.allowEmptyShould(true);
	}

	static ArchRule modulesTalkOnlyThroughApplicationOrEvents(String basePackage) {
		return classes().that().resideInAPackage(basePackage + ".(*)..")
				.should(onlyReachOtherModulesThroughApplicationOrEvents(basePackage))
				.because("modules are isolated: another module's domain and infrastructure are private to it")
				.allowEmptyShould(true);
	}

	private static ArchCondition<JavaClass> onlyReachOtherModulesThroughApplicationOrEvents(String basePackage) {
		return new ArchCondition<>("only depend on other modules through their application package or domain events") {
			@Override
			public void check(JavaClass origin, ConditionEvents events) {
				Optional<String> originModule = moduleOf(basePackage, origin);
				if (originModule.isEmpty()) {
					return;
				}
				for (Dependency dependency : origin.getDirectDependenciesFromSelf()) {
					JavaClass target = dependency.getTargetClass();
					Optional<String> targetModule = moduleOf(basePackage, target);
					boolean crossesModules = targetModule.isPresent()
							&& !targetModule.equals(originModule)
							&& !targetModule.get().equals(SHARED_KERNEL);
					if (crossesModules && !isPublicApi(basePackage, targetModule.get(), target)) {
						events.add(SimpleConditionEvent.violated(dependency, dependency.getDescription()));
					}
				}
			}
		};
	}

	private static boolean isPublicApi(String basePackage, String module, JavaClass target) {
		String applicationPackage = basePackage + "." + module + ".application";
		return target.getPackageName().equals(applicationPackage)
				|| target.getPackageName().startsWith(applicationPackage + ".")
				|| target.isAssignableTo(DomainEvent.class);
	}

	// "<base>.scheduling.domain.Session" -> "scheduling"; classes directly in <base> belong to no module.
	private static Optional<String> moduleOf(String basePackage, JavaClass javaClass) {
		String packageName = javaClass.getPackageName();
		if (!packageName.startsWith(basePackage + ".")) {
			return Optional.empty();
		}
		String rest = packageName.substring(basePackage.length() + 1);
		int dot = rest.indexOf('.');
		return Optional.of(dot < 0 ? rest : rest.substring(0, dot));
	}

}
