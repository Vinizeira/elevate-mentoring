package com.elevatementoring.backend.shared.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.elevatementoring.backend.support.AbstractIntegrationTest;

@AutoConfigureMockMvc
class OpenApiDocsTest extends AbstractIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	void apiDocsArePublic() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.info.title").value("Elevate Mentoring API"));
	}

	@Test
	void swaggerUiIsPublic() throws Exception {
		mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
	}

	@Test
	void otherEndpointsRequireAuthentication() throws Exception {
		mockMvc.perform(get("/anything")).andExpect(status().isUnauthorized());
	}

}
