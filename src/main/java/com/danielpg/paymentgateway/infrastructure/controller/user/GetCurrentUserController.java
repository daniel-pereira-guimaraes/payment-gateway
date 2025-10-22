package com.danielpg.paymentgateway.infrastructure.controller.user;

import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.application.user.GetCurrentUserUseCase;
import com.danielpg.paymentgateway.domain.user.User;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/users/me")
@Tag(name = "01 - Usuários")
public class GetCurrentUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetCurrentUserController.class);

    private final GetCurrentUserUseCase useCase;
    private final RequesterProvider requesterProvider;

    public GetCurrentUserController(GetCurrentUserUseCase useCase,
                                    RequesterProvider requesterProvider) {
        this.useCase = useCase;
        this.requesterProvider = requesterProvider;
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Consulta dados do usuário autenticado",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuário retornado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)
                            )
                    )
            }
    )
    @UnauthorizedResponse
    @BadRequestResponse
    public ResponseEntity<Response> get() {
        LOGGER.info("Obtendo dados do usuário corrente: userId={}", requesterProvider.requesterId());
        var user = useCase.getCurrentUser();
        return ResponseEntity.ok(Response.of(user));
    }

    @Schema(name = "CurrentUserResponse")
    public record Response(
            @Schema(description = "ID do usuário", example = "123")
            Long id,
            @Schema(description = "Nome completo do usuário", example = "João da Silva")
            String name,
            @Schema(description = "CPF do usuário", example = "00000000191")
            String cpf,
            @Schema(description = "Endereço de emailAddress do usuário", example = "joao@mail.com")
            String emailAddress,
            @Schema(description = "Saldo disponível do usuário", example = "12.01")
            BigDecimal balance) {

        public static Response of(User user) {
            return new Response(
                    user.id().value(),
                    user.name().value(),
                    user.cpf().value(),
                    user.emailAddress().value(),
                    user.balance().value()
            );
        }
    }

}
