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
        responseCode = "403",
        description = "Acesso proibido / usuário não autorizado a executar a ação",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                        implementation = AppErrorResponse.class,
                        example = "Mensagem de erro"
                )
        )
)
public @interface ForbiddenResponse {}
