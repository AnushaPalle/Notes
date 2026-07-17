package com.pulseboard.core.domain;

public enum Severity {
    INFO, WARN, ERROR;

    public boolean atLeast(Severity other) {
        return compareTo(other) >= 0;
    }
}
