package com.danielpg.paymentgateway.infrastructure.configuration;

import io.swagger.v3.oas.annotations.media.Schema;

public record AppErrorResponse(
        @Schema(example = "Mensagem do erro ocorrido")
        String message) {
    public AppErrorResponse(Exception e) {
        this(e.getMessage());
    }
}
