package com.danielpg.paymentgateway.infrastructure.controller.charge;

import com.danielpg.paymentgateway.application.charge.CancelChargeUseCase;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.infrastructure.configuration.AppErrorResponse;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.BadRequestResponse;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.ForbiddenResponse;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.UnauthorizedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "04 - Cobranças")
@RestController
@RequestMapping("/charges")
public class CancelChargeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancelChargeController.class);

    @Autowired
    private CancelChargeUseCase useCase;

    @PatchMapping("/{id}/cancel")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Cancela uma cobrança",
            description = "Cancela uma cobrança existente pelo seu ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Cobrança cancelada com sucesso"
                    ),
                    @ApiResponse(responseCode = "404", description = "Cobrança não encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppErrorResponse.class)
                            )
                    )
            }
    )
    @UnauthorizedResponse
    @BadRequestResponse
    @ForbiddenResponse
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        LOGGER.info("Cancelando cobrança: id={}", id);
        useCase.cancelCharge(ChargeId.of(id));
        LOGGER.info("Cobrança cancelada: id={}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
