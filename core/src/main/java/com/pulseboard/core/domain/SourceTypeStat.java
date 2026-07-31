package com.pulseboard.core.domain;

/** Read-model row: how many events of a type a source has produced. */
public record SourceTypeStat(String source, String type, long total) {
}
