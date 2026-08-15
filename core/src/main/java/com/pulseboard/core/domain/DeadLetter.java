package com.pulseboard.core.domain;

import java.time.Instant;

/** An event that could not be processed after all retries; kept for inspection and manual replay. */
public record DeadLetter(ActivityEvent event, String error, int attempts, Instant failedAt) {
}
