package com.pulseboard.core.adapter.out.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pulseboard.core.application.port.in.IngestUseCase;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.Severity;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability
class ObservabilityTest {

    @Autowired MockMvc mvc;
    @Autowired IngestUseCase ingest;
    @Autowired MeterRegistry registry;

    @Test
    void healthProbesAreOpenWithoutApiKey() throws Exception {
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
    }

    @Test
    void ingestOutcomesAreCountedAndExposedToPrometheus() throws Exception {
        double before = registry.counter("pulseboard.events.ingested", "result", "accepted").count();
        ingest.ingest(new ActivityEvent(UUID.randomUUID().toString(), "metrics-test", "ping",
                Severity.INFO, "{}", Instant.now()));
        double after = registry.counter("pulseboard.events.ingested", "result", "accepted").count();
        assertThat(after).isEqualTo(before + 1);

        mvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("pulseboard_events_ingested_total")));
    }
}
