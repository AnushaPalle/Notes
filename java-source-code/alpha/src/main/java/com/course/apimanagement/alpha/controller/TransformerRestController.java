package com.course.apimanagement.alpha.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.apimanagement.alpha.domain.Employee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Transformer", description = "API for transformation plugin demo")
public class TransformerRestController {

	private final List<Employee> employees = new ArrayList<>();

	@PostMapping(value = {
			"/echo" }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	@Operation(summary = "Echo the HTTP requests. Will shows : request body, headers, and query parameters")
	public ResponseEntity<String> echo(HttpServletRequest request, @RequestBody String body) {
		var response = new StringBuilder();

		response.append("Request body is:");
		response.append(StringUtils.LF);
		response.append(body);
		response.append(StringUtils.LF);
		response.append(StringUtils.LF);
		response.append("-----------------------------------------------------------------------");
		response.append(StringUtils.LF);
		response.append("HTTP request headers are:");
		response.append(StringUtils.LF);
		response.append(extractHttpHeaders(request).toString());
		response.append(StringUtils.LF);
		response.append(StringUtils.LF);
		response.append("-----------------------------------------------------------------------");
		response.append(StringUtils.LF);
		response.append("HTTP query params are:");
		response.append(StringUtils.LF);
		response.append(request.getQueryString());

		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(response.toString());
	}

	@GetMapping("/employee")
	@Operation(summary = "Get random employee")
	public ResponseEntity<Employee> employee() {
		return ResponseEntity.ok().body(employees.get(ThreadLocalRandom.current().nextInt(employees.size())));
	}

	private String extractHttpHeaders(HttpServletRequest request) {
		var sb = new StringBuilder();

		request.getHeaderNames().asIterator().forEachRemaining(h -> {
			request.getHeaders(h).asIterator().forEachRemaining(hv -> sb.append(h + " : " + hv + StringUtils.LF));
		});

		return sb.toString();
	}

	@PostConstruct
	private void postConstruct() {
		employees.add(new Employee("poseidon@athena.com", "Poseidon", 13897));
		employees.add(new Employee("hera@athena.com", "Hera", 8295));
		employees.add(new Employee("artemis@athena.com", "Artemis", 5930));
		employees.add(new Employee("hades@athena.com", "Hades", 13057));
		employees.add(new Employee("ares@athena.com", "Ares", 9590));
	}

}
