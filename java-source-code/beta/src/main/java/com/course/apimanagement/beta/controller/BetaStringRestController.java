package com.course.apimanagement.beta.controller;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.apimanagement.beta.domain.SimpleMessage;
import com.course.apimanagement.beta.util.DelayUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Beta String", description = "Beta string API")
public class BetaStringRestController {

	@GetMapping(path = "/fast", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fast response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
	public SimpleMessage fast() {
		return new SimpleMessage("Fast response from Beta");
	}

	@GetMapping(path = "/slow", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Slow response, takes 1-3 seconds")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
	public SimpleMessage slow() {
		DelayUtil.delay(1000, 3000);
		return new SimpleMessage("Slow response from Beta");
	}

	@GetMapping(path = "/who-am-i", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get current IP address")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
	public SimpleMessage whoAmI() throws SocketException, UnknownHostException {
		return new SimpleMessage("Beta on " + InetAddress.getLocalHost().getHostAddress());
	}

	@GetMapping(path = "/fast-random", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fast response, produce random HTTP status.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "415", description = "Unsupported media type"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<SimpleMessage> fastWithRandomStatus() {
		return responseWithRandomStatus();
	}

	@GetMapping(path = "/slow-random", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Slow response, takes 1-3 seconds. Produce random HTTP status.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "415", description = "Unsupported media type"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<SimpleMessage> slowWithRandomStatus() {
		DelayUtil.delay(1000, 3000);
		return responseWithRandomStatus();
	}

	private ResponseEntity<SimpleMessage> responseWithRandomStatus() {
		var random = ThreadLocalRandom.current().nextInt(100);

		switch (random % 10) {
		case 0:
			return ResponseEntity.badRequest().body(null);
		case 1:
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		case 2:
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
		case 3:
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		default:
			return ResponseEntity.ok(new SimpleMessage("Response from Beta"));
		}
	}

	@PutMapping(value = {
			"/echo-body" }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	@Operation(summary = "Echo the HTTP request body.")
	public ResponseEntity<String> echo(HttpServletRequest request, @RequestBody String body) {
		var response = new StringBuilder();

		response.append("Request body is:");
		response.append(StringUtils.LF);
		response.append(body);

		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(response.toString());
	}

}
