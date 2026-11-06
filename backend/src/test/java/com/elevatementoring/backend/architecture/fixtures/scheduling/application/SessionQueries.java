package com.elevatementoring.backend.architecture.fixtures.scheduling.application;

import com.elevatementoring.backend.architecture.fixtures.scheduling.infrastructure.SessionJpaRepository;

// Violation: application depends on infrastructure instead of a port.
public class SessionQueries {

	SessionJpaRepository repository;

}
