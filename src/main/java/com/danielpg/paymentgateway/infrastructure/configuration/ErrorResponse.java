package com.danielpg.paymentgateway.infrastructure.configuration;

public record ErrorResponse(String message) {

    public ErrorResponse(Exception e) {
        this(e.getMessage());
    }
}
