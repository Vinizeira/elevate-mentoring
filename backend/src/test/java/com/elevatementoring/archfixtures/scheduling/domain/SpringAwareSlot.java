package com.elevatementoring.archfixtures.scheduling.domain;

import org.springframework.context.ApplicationEventPublisher;

// Violation: domain depends on Spring.
public class SpringAwareSlot {

	ApplicationEventPublisher publisher;

}
