package com.elevatementoring.archfixtures.scheduling.application;

import com.elevatementoring.archfixtures.scheduling.infrastructure.SessionJpaRepository;

// Violation: application depends on infrastructure instead of a port.
public class SessionQueries {

	SessionJpaRepository repository;

}
