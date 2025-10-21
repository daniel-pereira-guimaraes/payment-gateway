package com.danielpg.paymentgateway.infrastructure.controller.charge.payment;

import com.danielpg.paymentgateway.application.charge.payment.RegisterPaymentUseCase;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.payment.Payment;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentMethod;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardCvv;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardExpirationDate;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardNumber;
import com.danielpg.paymentgateway.infrastructure.configuration.AppErrorResponse;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.BadRequestResponse;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.ForbiddenResponse;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.UnauthorizedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "05 - Pagamentos")
public class RegisterPaymentController {

    @Autowired
    private RegisterPaymentUseCase useCase;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Registra o pagamento de uma cobrança",
            description = "Registra um pagamento com saldo próprio ou cartão.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Dados necessários para registrar o pagamento",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Request.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Pagamento com saldo",
                                            value = """
                                            {
                                              "chargeId": 1,
                                              "method": "BALANCE"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Pagamento com cartão",
                                            value = """
                                            {
                                              "chargeId": 2,
                                              "method": "CREDIT_CARD",
                                              "creditCard": {
                                                "number": "4111111111111111",
                                                "expirationDate": "05/2030",
                                                "cvv": "123"
                                              }
                                            }
                                            """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Pagamento registrado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Emitente ou pagador não cadastrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Pagamento não autorizado",
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
    public ResponseEntity<Response> post(@RequestBody Request request) {
        var payment = useCase.registerPayment(request.toUseCaseRequest());
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(payment));
    }

    @Schema(name = "RegisterPaymentRequest")
    public record Request(
            @Schema(description = "Identificador da cobrança criada", example = "1")
            Long chargeId,
            @Schema(description = "Método de pagamento", allowableValues = {"BALANCE", "CREDIT_CARD"})
            PaymentMethod method,
            @Schema(description = "Dados do cartão (obrigatório somente para CREDIT_CARD)")
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
            @Schema(description = "Número do cartão", example = "4111111111111111")
            String number,
            @Schema(description = "Data de expiração do cartão no formato MM/AA ou MM/AAAA", example = "05/2030")
            String expirationDate,
            @Schema(description = "Código de segurança (CVV) do cartão", example = "123")
            String cvv) {

        CreditCard toDomain() {
            return CreditCard.builder()
                    .withNumber(CreditCardNumber.of(number))
                    .withExpirationDate(CreditCardExpirationDate.of(expirationDate))
                    .withCvv(CreditCardCvv.of(cvv))
                    .build();
        }
    }

    @Schema(name = "RegisterPaymentResponse")
    public record Response(
            @Schema(description = "Identificador do pagamento criado", example = "456")
            Long id,
            @Schema(description = "Identificador da cobrança paga", example = "123")
            Long chargeId,
            @Schema(description = "Método de pagamento", allowableValues = {"BALANCE", "CREDIT_CARD"})
            PaymentMethod method,
            @Schema(description = "Data e hora do pagamento, em epoch time", example = "1700007201")
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
