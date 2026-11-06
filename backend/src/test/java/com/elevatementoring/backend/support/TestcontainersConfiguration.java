package com.elevatementoring.backend.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

/**
 * Same images as infra/docker-compose.yml, so tests run against what we run locally.
 * Containers are reused between runs when ~/.testcontainers.properties has testcontainers.reuse.enable=true.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	// Tests run from backend/, so the compose init script is one level up.
	private static final String LOCALSTACK_INIT_SCRIPT = "../infra/localstack/init-aws.sh";

	@Bean
	@ServiceConnection
	PostgreSQLContainer postgresContainer() {
		return new PostgreSQLContainer("postgres:16-alpine")
				.withReuse(true);
	}

	@Bean
	@ServiceConnection
	LocalStackContainer localStackContainer() {
		return new LocalStackContainer("localstack/localstack:4.9")
				.withServices("sqs", "s3", "ses")
				.withCopyFileToContainer(MountableFile.forHostPath(LOCALSTACK_INIT_SCRIPT, 0755),
						"/etc/localstack/init/ready.d/init-aws.sh")
				// The init script creates this marker as its last step, so queues and buckets exist once it shows up.
				.waitingFor(Wait.forSuccessfulCommand("test -f /tmp/init-aws.done"))
				.withReuse(true);
	}

}
