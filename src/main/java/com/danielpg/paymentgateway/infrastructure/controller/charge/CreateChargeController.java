package com.danielpg.paymentgateway.infrastructure.controller.charge;

import com.danielpg.paymentgateway.application.charge.CreateChargeUseCase;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeDescription;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.shared.DataMasking;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.user.Cpf;
import com.danielpg.paymentgateway.infrastructure.configuration.AppErrorResponse;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.BadRequestResponse;
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

import java.math.BigDecimal;

@Tag(name = "04 - Cobranças")
@RestController
@RequestMapping("/charges")
public class CreateChargeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateChargeController.class);

    private final CreateChargeUseCase useCase;
    private final RequesterProvider requesterProvider;

    public CreateChargeController(CreateChargeUseCase useCase, RequesterProvider requesterProvider) {
        this.useCase = useCase;
        this.requesterProvider = requesterProvider;
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Cria uma cobrança",
            description = "Cria uma nova cobrança para um usuário específico",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pagador não cadastrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppErrorResponse.class)
                            )
                    )
            }
    )
    @UnauthorizedResponse
    @BadRequestResponse
    public ResponseEntity<Response> post(@RequestBody Request request) {
        LOGGER.info("Criando cobrança: userId={}, payerCpf={}, amount={}, description={}",
                requesterProvider.requesterId(),
                DataMasking.maskCpf(request.payerCpf),
                request.amount, request.description
        );
        var charge = useCase.createCharge(request.toUseCaseRequest());
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(charge));
    }

    @Schema(name = "CreateChargeRequest")
    public record Request(
            @Schema(description = "CPF do pagador", example = "32132132178")
            String payerCpf,
            @Schema(description = "Valor da cobrança", example = "5000.00")
            BigDecimal amount,
            @Schema(description = "Descrição da cobrança", example = "Compra de material de escritório", nullable = true)
            String description) {

        CreateChargeUseCase.Request toUseCaseRequest() {
            return new CreateChargeUseCase.Request(
                    Cpf.of(payerCpf),
                    PositiveMoney.of(amount),
                    ChargeDescription.ofNullable(description).orElse(null)
            );
        }
    }

    @Schema(name = "CreateChargeResponse")
    public record Response(
            @Schema(description = "ID da cobrança", example = "1")
            Long id,
            @Schema(description = "ID do emissor da cobrança", example = "1")
            Long issuerId,
            @Schema(description = "ID do pagador da cobrança", example = "2")
            Long payerId,
            @Schema(description = "Valor da cobrança", example = "5000.00")
            BigDecimal amount,
            @Schema(description = "Descrição da cobrança", example = "Compra de material de escritório")
            String description,
            @Schema(description = "Timestamp de criação", example = "1700000000")
            Long createdAt,
            @Schema(description = "Timestamp de vencimento", example = "1700003600")
            Long dueAt,
            @Schema(description = "Status da cobrança", example = "PENDING")
            ChargeStatus status) {

        public static Response of(Charge charge) {
            return new Response(
                    charge.id().value(),
                    charge.issuerId().value(),
                    charge.payerId().value(),
                    charge.amount().value(),
                    charge.description() == null ? null : charge.description().value(),
                    charge.createdAt().value(),
                    charge.dueAt().value(),
                    charge.status()
            );
        }
    }
}
