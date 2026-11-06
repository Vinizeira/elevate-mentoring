package com.elevatementoring.backend.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.UUID;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

class SqsSmokeTest extends AbstractIntegrationTest {

	@Autowired
	private SqsTemplate sqsTemplate;

	@Test
	void sendsAndReceivesMessageThroughLocalStack() {
		String queue = "notification-events";
		String payload = "smoke-" + UUID.randomUUID();

		sqsTemplate.send(queue, payload);

		var received = sqsTemplate.receive(from -> from.queue(queue).pollTimeout(Duration.ofSeconds(5)), String.class);
		assertThat(received).map(Message::getPayload).contains(payload);
	}

}
