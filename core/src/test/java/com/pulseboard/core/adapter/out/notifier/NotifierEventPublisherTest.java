package com.pulseboard.core.adapter.out.notifier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

class NotifierEventPublisherTest {

    private MockRestServiceServer server;
    private NotifierEventPublisher publisher;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://notifier");
        server = MockRestServiceServer.bindTo(builder).build();
        publisher = new NotifierEventPublisher(builder.build());
    }

    private static ActivityEvent event(Severity severity) {
        return new ActivityEvent("e1", "billing", "invoice.failed", severity, "", null);
    }

    @Test
    void errorEventsAreSentToTheNotifier() {
        server.expect(requestTo("http://notifier/notify")).andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eventId").value("e1"))
                .andRespond(withSuccess());

        publisher.publish(event(Severity.ERROR));

        server.verify();
    }

    @Test
    void infoEventsAreNotSent() {
        publisher.publish(event(Severity.INFO));

        server.verify(); // no expectations registered, so any request would have failed
    }

    @Test
    void notifierFailureSurfacesSoTheCoreCanLogIt() {
        server.expect(requestTo("http://notifier/notify")).andRespond(withServerError());

        assertThrows(RestClientException.class, () -> publisher.publish(event(Severity.ERROR)));
    }
}
