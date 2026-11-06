package com.elevatementoring.backend.shared.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.elevatementoring.backend.shared.domain.ConflictException;
import com.elevatementoring.backend.shared.domain.DomainException;
import com.elevatementoring.backend.shared.domain.NotFoundException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.ThrowingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandlerTest.ThrowingController.class)
class GlobalExceptionHandlerTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	void domainExceptionReturns422() throws Exception {
		mockMvc.perform(get("/test/domain"))
			.andExpect(status().isUnprocessableContent())
			.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
			.andExpect(jsonPath("$.status").value(422))
			.andExpect(jsonPath("$.detail").value("slot is in the past"));
	}

	@Test
	void conflictExceptionReturns409() throws Exception {
		mockMvc.perform(get("/test/conflict"))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.detail").value("slot already booked"));
	}

	@Test
	void notFoundExceptionReturns404() throws Exception {
		mockMvc.perform(get("/test/not-found"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.detail").value("mentor not found"));
	}

	@Test
	void invalidBodyReturns400WithFieldErrors() throws Exception {
		mockMvc.perform(post("/test/validation").contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"name": "", "email": "not-an-email"}
						"""))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
			.andExpect(jsonPath("$.errors.name").exists())
			.andExpect(jsonPath("$.errors.email").exists());
	}

	@RestController
	static class ThrowingController {

		@GetMapping("/test/domain")
		void domain() {
			throw new DomainException("slot is in the past");
		}

		@GetMapping("/test/conflict")
		void conflict() {
			throw new ConflictException("slot already booked");
		}

		@GetMapping("/test/not-found")
		void notFound() {
			throw new NotFoundException("mentor not found");
		}

		@PostMapping("/test/validation")
		void validation(@Valid @RequestBody Request request) {
		}

		record Request(@NotBlank String name, @Email String email) {
		}

	}

}
