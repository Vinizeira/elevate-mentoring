package com.elevatementoring.backend.shared.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class FlywayMigrationTest {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Test
	void v1EnablesRequiredExtensions() {
		List<String> extensions = jdbcTemplate.queryForList(
				"SELECT extname FROM pg_extension WHERE extname IN ('btree_gist', 'pgcrypto')", String.class);

		assertThat(extensions).containsExactlyInAnyOrder("btree_gist", "pgcrypto");
	}

}
