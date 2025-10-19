package com.danielpg.paymentgateway.infrastructure.controller.charge;

import com.danielpg.paymentgateway.application.charge.CreateChargeUseCase;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.user.Cpf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/charges")
public class CreateChargeController {

    @Autowired
    private CreateChargeUseCase useCase;

    @PostMapping
    public ResponseEntity<Response> post(@RequestBody Request request) {
        var charge = useCase.createCharge(request.toUseCaseRequest());
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(charge));
    }

    public record Request(
            String payerCpf,
            BigDecimal amount,
            String description) {

        CreateChargeUseCase.Request toUseCaseRequest() {
            return new CreateChargeUseCase.Request(
                    Cpf.of(payerCpf),
                    PositiveMoney.of(amount),
                    description
            );
        }
    }

    public record Response(
            Long id,
            Long issuerId,
            Long payerId,
            BigDecimal amount,
            String description,
            Long createdAt,
            Long dueAt,
            ChargeStatus status) {

        public static Response of(Charge charge) {
            return new Response(
                    charge.id().value(),
                    charge.issuerId().value(),
                    charge.payerId().value(),
                    charge.amount().value(),
                    charge.description(),
                    charge.createdAt().value(),
                    charge.dueAt().value(),
                    charge.status()
            );
        }
    }
}
