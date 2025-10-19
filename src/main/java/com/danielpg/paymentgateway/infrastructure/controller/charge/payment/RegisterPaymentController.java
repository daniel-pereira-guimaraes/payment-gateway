package com.danielpg.paymentgateway.infrastructure.controller.charge.payment;

import com.danielpg.paymentgateway.application.charge.payment.RegisterPaymentUseCase;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.payment.Payment;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentMethod;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardCvv;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardExpirationDate;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class RegisterPaymentController {

    @Autowired
    private RegisterPaymentUseCase useCase;

    @PostMapping
    public ResponseEntity<Response> post(@RequestBody Request request) {
        var payment = useCase.registerPayment(request.toUseCaseRequest());
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(payment));
    }

    public record Request(
            Long chargeId,
            PaymentMethod method,
            RequestCreditCard creditCard) {

        public RegisterPaymentUseCase.Request toUseCaseRequest() {
            return new RegisterPaymentUseCase.Request(
                    ChargeId.of(chargeId),
                    method,
                    creditCard != null ? creditCard.toDomain() : null
            );
        }
    }

    public record RequestCreditCard(
            String number,
            String expirationDate,
            String cvv) {

        CreditCard toDomain() {
            return CreditCard.builder()
                    .withNumber(CreditCardNumber.of(number))
                    .withExpirationDate(CreditCardExpirationDate.of(expirationDate))
                    .withCvv(CreditCardCvv.of(cvv))
                    .build();
        }
    }

    public record Response(
            Long id,
            Long chargeId,
            PaymentMethod method,
            Long paidAt) {

        public static Response of(Payment payment) {
            return new Response(
                    payment.id().value(),
                    payment.chargeId().value(),
                    payment.method(),
                    payment.paidAt().value()
            );
        }
    }
}
