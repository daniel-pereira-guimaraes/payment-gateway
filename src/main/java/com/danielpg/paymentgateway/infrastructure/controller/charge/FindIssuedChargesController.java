package com.danielpg.paymentgateway.infrastructure.controller.charge;

import com.danielpg.paymentgateway.application.charge.FindIssuedChargesUseCase;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesItem;
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
@RequestMapping("/charges/issued")
public class FindIssuedChargesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindIssuedChargesController.class);

    private final FindIssuedChargesUseCase useCase;
    private final RequesterProvider requesterProvider;

    public FindIssuedChargesController(FindIssuedChargesUseCase useCase,
                                       RequesterProvider requesterProvider) {
        this.useCase = useCase;
        this.requesterProvider = requesterProvider;
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Consulta de cobranças emitidas",
            description = "Retorna as cobranças emitidas. É possível filtrar por status (opcional).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cobranças retornadas com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)
                            )
                    )
            }
    )
    @UnauthorizedResponse
    @BadRequestResponse
    public ResponseEntity<Response> get(
            @Parameter(
                    description = "Status das cobranças separados por vírgula (opcional)",
                    example = "PENDING,PAID"
            )
            @RequestParam(value = "statuses", required = false) String statusCsv) {
        LOGGER.info("Consultando cobranças emitidas: userId={}", requesterProvider.requesterId());
        var statuses = ChargeStatus.fromCsv(statusCsv);
        var response = useCase.find(statuses);
        return ResponseEntity.ok(Response.of(response));
    }

    @Schema(name = "IssuedChargesResponse")
    public record Response(
            List<IssuedChargesItem> pendings,
            List<IssuedChargesItem> paids,
            List<IssuedChargesItem> canceleds) {

        public static Response of(FindIssuedChargesUseCase.Response useCaseResponse) {
            return new Response(
                    useCaseResponse.pendings(),
                    useCaseResponse.paids(),
                    useCaseResponse.canceleds()
            );
        }
    }

}
