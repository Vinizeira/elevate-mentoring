package com.elevatementoring.backend.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

/**
 * Base class for tests that touch infrastructure (database, SQS, S3, SES).
 * All subclasses share one Spring context, so the containers start once per test run.
 * MockMvc lives here too: adding it in a subclass would change the context key and start a second set of containers.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIntegrationTest {
}
