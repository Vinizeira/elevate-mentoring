package com.elevatementoring.backend.architecture.fixtures.scheduling.web;

import com.elevatementoring.backend.architecture.fixtures.scheduling.infrastructure.SessionJpaRepository;

// Violation: web skips application and reaches infrastructure.
public class SessionController {

	SessionJpaRepository repository;

}
