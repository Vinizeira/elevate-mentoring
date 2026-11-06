package com.elevatementoring.backend.architecture.fixtures.scheduling.domain;

import org.springframework.context.ApplicationEventPublisher;

// Violation: domain depends on Spring.
public class SpringAwareSlot {

	ApplicationEventPublisher publisher;

}
