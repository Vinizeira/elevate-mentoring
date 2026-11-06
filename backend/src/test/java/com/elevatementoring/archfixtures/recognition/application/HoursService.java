package com.elevatementoring.archfixtures.recognition.application;

import com.elevatementoring.archfixtures.scheduling.application.SessionQueries;
import com.elevatementoring.archfixtures.scheduling.domain.Session;
import com.elevatementoring.archfixtures.scheduling.domain.SessionCompleted;

public class HoursService {

	// Allowed: another module's application package.
	SessionQueries sessionQueries;

	// Allowed: a domain event published by another module.
	SessionCompleted lastCompleted;

	// Violation: another module's domain model is private to it.
	Session session;

}
