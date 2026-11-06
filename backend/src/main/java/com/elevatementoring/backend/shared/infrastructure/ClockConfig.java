package com.elevatementoring.backend.shared.infrastructure;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Inject {@link Clock} instead of calling {@code Instant.now()} so tests can use a fixed time.
 */
@Configuration
class ClockConfig {

	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}

}
