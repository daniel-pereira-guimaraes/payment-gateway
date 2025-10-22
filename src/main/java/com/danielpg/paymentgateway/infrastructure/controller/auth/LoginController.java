package com.danielpg.paymentgateway.infrastructure.controller.auth;

import com.danielpg.paymentgateway.application.auth.LoginUseCase;
import com.danielpg.paymentgateway.application.auth.Token;
import com.danielpg.paymentgateway.domain.shared.DataMasking;
import com.danielpg.paymentgateway.domain.user.*;
import com.danielpg.paymentgateway.infrastructure.configuration.AppErrorResponse;
import com.danielpg.paymentgateway.infrastructure.configuration.swagger.BadRequestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "02 - Autenticação")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginUseCase loginUseCase;

    @PostMapping("/login")
    @Operation(
            summary = "Login de usuário",
            description = "Autentica o usuário por CPF + senha ou E-mail + senha e retorna um token JWT",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informe CPF + senha ou E-mail + senha",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Login com CPF do João",
                                            value = """
                                                    {
                                                      "cpf": "12312312387",
                                                      "password": "Senha!12345"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Login com CPF da Maria",
                                            value = """
                                                    {
                                                      "cpf": "32132132178",
                                                      "password": "Senha!54321"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Login com E-mail do João",
                                            value = """
                                                    {
                                                      "emailAddress": "joao.silva@email.com",
                                                      "password": "Senha!12345"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Login com E-mail da Maria",
                                            value = """
                                                    {
                                                      "emailAddress": "maria@email.com",
                                                      "password": "Senha!54321"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login realizado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário não cadastrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppErrorResponse.class)
                            )
                    )
            }
    )
    @BadRequestResponse
    public ResponseEntity<Response> post(@RequestBody Request request) {
        LOGGER.info("Fazendo login: cpf={}, emailAddress={}",
                DataMasking.maskCpf(request.cpf),
                DataMasking.maskEmail(request.emailAddress)
        );
        var token = loginUseCase.login(request.toUseCaseRequest());
        return ResponseEntity.status(HttpStatus.OK).body(Response.of(token));
    }

    @Schema(name = "LoginRequest")
    public record Request(
            @Schema(description = "E-mail do usuário", example = "joao.silva@email.com")
            String emailAddress,
            @Schema(description = "CPF do usuário", example = "12312312387")
            String cpf,
            @Schema(description = "Senha do usuário", example = "Senha!12345")
            String password
    ) {
        public LoginUseCase.Request toUseCaseRequest() {
            return new LoginUseCase.Request(
                    EmailAddress.ofNullable(emailAddress).orElse(null),
                    Cpf.ofNullable(cpf).orElse(null),
                    PlainTextPassword.ofNullable(password).orElse(null)
            );
        }
    }

    @Schema(name = "LoginResponse")
    public record Response(
            @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            String token
    ) {
        public static Response of(Token token) {
            return new Response(token.rawToken());
        }
    }
}
