package com.course.apimanagement.omega.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.course.apimanagement.omega.domain.SimpleMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/chain")
@Tag(name = "Omega Chain Call", description = "One call to endpoint in this service will calls several other service on background.")
public class OmegaChainRestController {

	@Autowired
	private WebClient webClient;

	@GetMapping(path = "/async", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@Operation(summary = "Chain call (async)", description = "Multiple API calls on background, each call is asynchronous. "
			+ "Means call (n+1) will be executed after call (n) executed, eventhough call (n) has not returned the value dure to slow process. "
			+ "Each call will returns HTTP status 200 OK")
	public Flux<SimpleMessage> chainAsync() {
		var request1 = makeRequest();
		var request2 = makeRequest();
		var request3 = makeRequest();
		var request4 = makeRequest();
		var request5 = makeRequest();

		return Flux.merge(request1, request2, request3, request4, request5);
	}

	@GetMapping(path = "/sync", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Chain call (sync)", description = "Multiple API calls on background, each call is synchronous. "
			+ "Means call (n+1) will be executed after call (n) returns respose. "
			+ "Each call will returns HTTP status 200 OK")
	public List<SimpleMessage> chainSync() {
		var request1 = makeRequest();
		var request2 = makeRequest();
		var request3 = makeRequest();
		var request4 = makeRequest();
		var request5 = makeRequest();

		return Flux.concat(request1, request2, request3, request4, request5).collectList().block();
	}

	private Mono<SimpleMessage> makeRequest() {
		return webClient.get().uri(randomEndpoint()).retrieve().bodyToMono(SimpleMessage.class);
	}

	@GetMapping(path = "/random-status", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Chain call (sync) with random status", description = "Multiple calls, each call is synchronous. "
			+ "Eech API call can returns response with random HTTP status response "
			+ "(200 OK / 403 Forbidden / 415 Unsupported media type / 500 Internal server error).")
	public List<SimpleMessage> chainRandomStatusSync() {
		var list = new ArrayList<SimpleMessage>();

		for (int i = 0; i < 5; i++) {
			list.add(webClient.get().uri(randomEndpointWithStatus()).retrieve().bodyToMono(SimpleMessage.class)
					.block(Duration.ofSeconds(15)));
		}

		return list;
	}

	private String randomEndpoint() {
		var sb = new StringBuilder();
		var random = ThreadLocalRandom.current().nextInt(4);

		switch (random) {
		case 0:
			sb.append(ServiceAddress.ALPHA);
			break;
		case 1:
			sb.append(ServiceAddress.BETA);
			break;
		case 2:
			sb.append(ServiceAddress.GAMMA);
			break;
		default:
			sb.append(ServiceAddress.OMEGA);
			break;
		}

		sb.append(random % 2 == 0 ? "/api/v1/fast" : "/api/v1/slow");

		return sb.toString();
	}

	private String randomEndpointWithStatus() {
		var sb = new StringBuilder();
		var random = ThreadLocalRandom.current().nextInt(4);

		switch (random) {
		case 0:
			sb.append(ServiceAddress.ALPHA);
			break;
		case 1:
			sb.append(ServiceAddress.BETA);
			break;
		case 2:
			sb.append(ServiceAddress.GAMMA);
			break;
		default:
			sb.append(ServiceAddress.OMEGA);
			break;
		}

		sb.append(random % 2 == 0 ? "/api/v1/fast-random" : "/api/v1/slow-random");

		return sb.toString();
	}

}
