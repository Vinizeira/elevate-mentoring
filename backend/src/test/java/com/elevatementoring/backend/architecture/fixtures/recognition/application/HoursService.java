package com.elevatementoring.backend.architecture.fixtures.recognition.application;

import com.elevatementoring.backend.architecture.fixtures.scheduling.application.SessionQueries;
import com.elevatementoring.backend.architecture.fixtures.scheduling.domain.Session;
import com.elevatementoring.backend.architecture.fixtures.scheduling.domain.SessionCompleted;

public class HoursService {

	// Allowed: another module's application package.
	SessionQueries sessionQueries;

	// Allowed: a domain event published by another module.
	SessionCompleted lastCompleted;

	// Violation: another module's domain model is private to it.
	Session session;

}
