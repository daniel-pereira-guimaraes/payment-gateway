package com.danielpg.paymentgateway.infrastructure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
@Tag(name = "06 - Monitoramento")
public class PingController {

    @Operation(
            summary = "Verifica disponibilidade do serviço",
            description = "Retorna 'pong' se a aplicação estiver funcionando corretamente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Serviço disponível",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "pong")
                    )
            )
    })
    @GetMapping
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
