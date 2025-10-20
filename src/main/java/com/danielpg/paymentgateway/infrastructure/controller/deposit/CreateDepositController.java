package com.danielpg.paymentgateway.infrastructure.controller.deposit;

import com.danielpg.paymentgateway.application.deposit.CreateDepositUseCase;
import com.danielpg.paymentgateway.domain.deposit.Deposit;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "Depósito", description = "Criação de depósito.")
@RestController
@RequestMapping("/deposits")
public class CreateDepositController {

    @Autowired
    private CreateDepositUseCase useCase;

    @PostMapping
    @Operation(
            summary = "Cria um depósito",
            description = "Cria um depósito, cujo valor será somado ao saldo do usuário autenticado",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Depósito criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Valor inválido"),
                    @ApiResponse(responseCode = "403", description = "Usuário não autenticado"),
                    @ApiResponse(responseCode = "409", description = "Depósito não autorizado")
            }
    )
    public ResponseEntity<Response> post(@RequestBody Request request) {
        var deposit = useCase.createDeposit(PositiveMoney.of(request.amount));
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(deposit));
    }

    public record Request(
            @NotNull
            BigDecimal amount
    ) {}

    public record Response(
            Long id,
            Long userId,
            BigDecimal amount,
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
