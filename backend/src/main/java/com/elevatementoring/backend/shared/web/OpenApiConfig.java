package com.elevatementoring.backend.shared.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * API metadata shown in Swagger UI ({@code /swagger-ui.html}) and in {@code /v3/api-docs}.
 */
@Configuration
class OpenApiConfig {

	@Bean
	OpenAPI elevateOpenApi() {
		return new OpenAPI().info(new Info()
			.title("Elevate Mentoring API")
			.description("Students book sessions with mentors; hours count once both confirm the session took place.")
			.version("v1"));
	}

}
