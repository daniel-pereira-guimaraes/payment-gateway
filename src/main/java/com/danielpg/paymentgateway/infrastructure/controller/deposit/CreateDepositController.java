package com.danielpg.paymentgateway.infrastructure.controller.deposit;

import com.danielpg.paymentgateway.application.deposit.CreateDepositUseCase;
import com.danielpg.paymentgateway.domain.deposit.Deposit;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/deposits")
public class CreateDepositController {

    @Autowired
    private CreateDepositUseCase useCase;

    @PostMapping
    public ResponseEntity<Response> post(@RequestBody Request request) {
        var deposit = useCase.createDeposit(PositiveMoney.of(request.amount));
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(deposit));
    }

    public record Request(BigDecimal amount) {
    }

    public record Response(
            Long id,
            Long userId,
            BigDecimal amount,
            Long createdAt) {

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
