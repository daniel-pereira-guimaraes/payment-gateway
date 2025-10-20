package com.danielpg.paymentgateway.infrastructure.controller.auth;

import com.danielpg.paymentgateway.application.auth.LoginUseCase;
import com.danielpg.paymentgateway.application.auth.Token;
import com.danielpg.paymentgateway.domain.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "02 - Autenticação", description = "Login de usuário.")
@RestController
@RequestMapping("/auth")
public class LoginController {

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
                            schema = @Schema(implementation = Request.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Login com CPF",
                                            value = """
                                            {
                                              "cpf": "12312312387",
                                              "password": "Senha!12345"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Login com E-mail",
                                            value = """
                                            {
                                              "emailAddress": "joao.silva@email.com",
                                              "password": "Senha!12345"
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
                                    schema = @Schema(implementation = Response.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados inválidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"message\": \"Dados inválidos\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciais inválidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"message\": \"Usuário ou senha inválida.\"}")
                            )
                    )
            }
    )
    public ResponseEntity<Response> post(@RequestBody Request request) {
        var token = loginUseCase.login(request.toUseCaseRequest());
        return ResponseEntity.status(HttpStatus.OK).body(Response.of(token));
    }

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
                    PlainTextPassword.of(password)
            );
        }
    }

    public record Response(
            @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            String token
    ) {
        public static Response of(Token token) {
            return new Response(token.rawToken());
        }
    }
}
