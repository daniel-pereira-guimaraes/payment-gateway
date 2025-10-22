package com.danielpg.paymentgateway.domain.charge;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ChargeStatus {
    PENDING,
    PAID,
    CANCELED;

    public static Set<ChargeStatus> fromCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(s -> {
                    try {
                        return ChargeStatus.valueOf(s);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Status inválido: " + s, e);
                    }
                })
                .collect(Collectors.toSet());
    }
}
