package com.elevatementoring.backend.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Base class for tests that touch infrastructure (database, SQS, S3, SES).
 * All subclasses share one Spring context, so the containers start once per test run.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIntegrationTest {
}
