package com.course.apimanagement.gamma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	private static final String API_INFO_DESCRIPTION = "<p>API documentations for service <code>gamma</code></p>";

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI().components(new Components())
				.info(new Info().description(API_INFO_DESCRIPTION).title("Gamma").version("2.0"));
	}

	@Bean
	ModelResolver jacksonModelResolver(ObjectMapper objectMapper) {
		return new ModelResolver(objectMapper);
	}

}
