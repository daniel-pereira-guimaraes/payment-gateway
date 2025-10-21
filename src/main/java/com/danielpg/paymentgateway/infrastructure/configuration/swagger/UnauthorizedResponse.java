package com.danielpg.paymentgateway.infrastructure.configuration.swagger;

import com.danielpg.paymentgateway.infrastructure.configuration.AppErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado / token ausente ou inválido",
        content = @Content()
)
public @interface UnauthorizedResponse {}
