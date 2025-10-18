package com.danielpg.paymentgateway.domain.shared;

public class Validation {

    private Validation() {
    }

    public static <T> T required(T value, String message) {
        checkNull(value, message);
        checkBlankString(value, message);
        return value;
    }

    private static void checkNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void checkBlankString(Object value, String message) {
        if (value instanceof String str && str.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
