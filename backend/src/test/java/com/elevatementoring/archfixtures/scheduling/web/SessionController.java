package com.elevatementoring.archfixtures.scheduling.web;

import com.elevatementoring.archfixtures.scheduling.infrastructure.SessionJpaRepository;

// Violation: web skips application and reaches infrastructure.
public class SessionController {

	SessionJpaRepository repository;

}
