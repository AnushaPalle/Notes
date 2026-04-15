package com.course.apimanagement.omega.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

	private ClientHttpConnector clientHttpConnector() {
		var client = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000).doOnConnected(
				connection -> connection.addHandlerLast(new ReadTimeoutHandler(10000, TimeUnit.MILLISECONDS))
						.addHandlerLast(new WriteTimeoutHandler(10000, TimeUnit.MILLISECONDS)));

		return new ReactorClientHttpConnector(client);
	}

	@Bean
	WebClient webClient(WebClient.Builder builder) {
		return builder.clientConnector(clientHttpConnector()).build();
	}

}
