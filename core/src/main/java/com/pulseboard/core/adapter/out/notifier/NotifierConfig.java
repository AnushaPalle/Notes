package com.pulseboard.core.adapter.out.notifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

/** Enabled only when PULSEBOARD_NOTIFIER_URL is set, so the core runs fine without a notifier. */
@Configuration
@ConditionalOnProperty(prefix = "pulseboard.notifier", name = "url")
class NotifierConfig {

    @Bean
    NotifierEventPublisher notifierEventPublisher(@Value("${pulseboard.notifier.url}") String url, ObjectMapper mapper) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2000);
        requestFactory.setReadTimeout(2000);
        RestClient client = RestClient.builder().baseUrl(url).requestFactory(requestFactory)
                .messageConverters(converters -> {
                    converters.clear();
                    converters.add(new MappingJackson2HttpMessageConverter(mapper));
                }).build();
        return new NotifierEventPublisher(client);
    }
}
