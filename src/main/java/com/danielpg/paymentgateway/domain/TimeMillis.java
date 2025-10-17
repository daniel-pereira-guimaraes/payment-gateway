package com.danielpg.paymentgateway.domain;

import java.util.Objects;
import java.util.Optional;

public class TimeMillis implements Comparable<TimeMillis> {

    private final Long value;

    private TimeMillis(Long value) {
        this.value = Validation.required(value, "O timestamp em milissegundos é requerido.");
    }

    public static TimeMillis of(Long value) {
        return new TimeMillis(value);
    }

    public static Optional<TimeMillis> ofNullable(Long value) {
        return value == null ? Optional.empty()
                : Optional.of(new TimeMillis(value));
    }

    public static TimeMillis now() {
        return new TimeMillis(System.currentTimeMillis());
    }

    public Long value() {
        return value;
    }

    public boolean isBefore(TimeMillis other) {
        return value < other.value;
    }

    public boolean isAfter(TimeMillis other) {
        return value > other.value;
    }

    public TimeMillis plusDays(int days) {
        long millisToAdd = days * 24L * 60 * 60 * 1000;
        return new TimeMillis(this.value + millisToAdd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TimeMillis otherTimeMillis
                && Objects.equals(value, otherTimeMillis.value);
    }

    @Override
    public int compareTo(TimeMillis other) {
        return Long.compare(value, other.value);
    }
}
