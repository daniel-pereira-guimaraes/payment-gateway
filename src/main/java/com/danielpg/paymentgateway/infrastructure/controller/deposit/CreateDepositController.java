package com.danielpg.paymentgateway.infrastructure.controller.deposit;

import com.danielpg.paymentgateway.application.deposit.CreateDepositUseCase;
import com.danielpg.paymentgateway.domain.deposit.Deposit;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.infrastructure.configuration.AppErrorResponse;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.BadRequestResponse;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.UnauthorizedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "03 - Depósitos")
@RestController
@RequestMapping("/deposits")
public class CreateDepositController {

    @Autowired
    private CreateDepositUseCase useCase;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Cria um depósito",
            description = "Cria um depósito, cujo valor será somado ao saldo do usuário autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Depósito criado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Depósito não autorizado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = AppErrorResponse.class
                                    )
                            )
                    )
            }
    )
    @UnauthorizedResponse
    @BadRequestResponse
    public ResponseEntity<Response> post(@RequestBody Request request) {
        var deposit = useCase.createDeposit(PositiveMoney.of(request.amount));
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(deposit));
    }

    @Schema(name = "CreateDepositRequest")
    public record Request(
            @NotNull
            @Schema(description = "Valor do depósito", example = "5000.00")
            BigDecimal amount
    ) {}

    @Schema(name = "CreateDepositResponse")
    public record Response(
            @Schema(description = "ID do depósito", example = "1")
            Long id,
            @Schema(description = "ID do usuário", example = "1")
            Long userId,
            @Schema(description = "Valor do depósito", example = "5000.00")
            BigDecimal amount,
            @Schema(description = "Timestamp de criação", example = "1700000000")
            Long createdAt
    ) {
        public static Response of(Deposit deposit) {
            return new Response(
                    deposit.id().value(),
                    deposit.userId().value(),
                    deposit.amount().value(),
                    deposit.createdAt().value()
            );
        }
    }
}
