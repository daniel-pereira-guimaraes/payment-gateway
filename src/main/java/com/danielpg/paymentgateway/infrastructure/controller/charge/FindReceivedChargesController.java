package com.danielpg.paymentgateway.infrastructure.controller.charge;

import com.danielpg.paymentgateway.application.charge.FindReceivedChargesUseCase;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesItem;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.BadRequestResponse;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.UnauthorizedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "04 - Cobranças")
@RestController
@RequestMapping("/charges/received")
public class FindReceivedChargesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindReceivedChargesController.class);

    private final FindReceivedChargesUseCase useCase;
    private final RequesterProvider requesterProvider;

    public FindReceivedChargesController(FindReceivedChargesUseCase useCase,
                                         RequesterProvider requesterProvider) {
        this.useCase = useCase;
        this.requesterProvider = requesterProvider;
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Consulta de cobranças recebidas",
            description = "Retorna as cobranças recebidas. É possível filtrar por status (opcional).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cobranças retornadas com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
            }
    )
    @UnauthorizedResponse
    @BadRequestResponse
    public ResponseEntity<Response> get(
            @Parameter(
                    description = "Status das cobranças separados por vírgula (opcional)",
                    example = "PAID,CANCELED"
            )
            @RequestParam(value = "statuses", required = false) String statusCsv) {
        LOGGER.info("Consultando cobranças recebidas: userId={}", requesterProvider.requesterId());
        var statuses = ChargeStatus.fromCsv(statusCsv);
        var response = useCase.find(statuses);
        return ResponseEntity.ok(Response.of(response));
    }

    @Schema(name = "ReceivedChargesResponse")
    public record Response(
        List<ReceivedChargesItem> pendings,
        List<ReceivedChargesItem> paids,
        List<ReceivedChargesItem> canceleds) {

        public static Response of(FindReceivedChargesUseCase.Response useCaseResponse) {
            return new Response(
                    useCaseResponse.pendings(),
                    useCaseResponse.paids(),
                    useCaseResponse.canceleds()
            );
        }
    }

}
