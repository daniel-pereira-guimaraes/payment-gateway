package com.danielpg.paymentgateway.infrastructure.controller.charge;

import com.danielpg.paymentgateway.application.charge.FindReceivedChargesUseCase;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cobrança", description = "Consulta de cobranças recebidas.")
@RestController
@RequestMapping("/charges/received")
public class FindReceivedChargesController {

    @Autowired
    private FindReceivedChargesUseCase useCase;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Busca cobranças recebidas",
            description = "Retorna as cobranças recebidas. É possível filtrar por status (opcional).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cobranças retornadas com sucesso", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Parâmetro inválido", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content)
            }
    )
    public ResponseEntity<FindReceivedChargesUseCase.Response> get(
            @Parameter(description = "Status das cobranças separados por vírgula (opcional)")
            @RequestParam(value = "statuses", required = false) String statusCsv) {
        var statuses = ChargeStatus.fromCsv(statusCsv);
        var response = useCase.find(statuses);
        return ResponseEntity.ok(response);
    }
}
