package com.shopflow.monolith.billing.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class RemotePaymentProcessorTest {

    @Test
    void mapsApprovedAndDeclinedResponsesAndSendsIdempotencyKey() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        var processor = new RemotePaymentProcessor(builder, "http://payments");

        server.expect(requestTo("http://payments/payments"))
                .andExpect(header("Idempotency-Key", "order-7"))
                .andRespond(withSuccess("{\"paymentId\":\"p1\",\"status\":\"APPROVED\"}", MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://payments/payments"))
                .andRespond(withSuccess("{\"paymentId\":\"p2\",\"status\":\"DECLINED\",\"reason\":\"nope\"}",
                        MediaType.APPLICATION_JSON));

        assertThat(processor.process(7L, new BigDecimal("10")).success()).isTrue();
        var declined = processor.process(8L, new BigDecimal("5000"));
        assertThat(declined.success()).isFalse();
        assertThat(declined.reason()).isEqualTo("nope");
        server.verify();
    }
}
