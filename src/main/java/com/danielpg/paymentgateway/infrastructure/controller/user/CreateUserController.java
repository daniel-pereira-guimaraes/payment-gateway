package com.danielpg.paymentgateway.infrastructure.controller.user;

import com.danielpg.paymentgateway.application.user.CreateUserUseCase;
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

@Tag(name = "01 - Usuários")
@RestController
@RequestMapping("/users")
public class CreateUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserController.class);

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @PostMapping
    @Operation(
            summary = "Cria um usuário",
            description = "Cria um novo usuário com nome, CPF, e-mail e senha",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do usuário",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Request.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                {
                                                  "name": "Joao Silva",
                                                  "cpf": "12312312387",
                                                  "emailAddress": "joao.silva@email.com",
                                                  "password": "Senha!12345"
                                                }
                                                """
                                    ),
                                    @ExampleObject(
                                            value = """
                                                {
                                                  "name": "Maria Souza",
                                                  "cpf": "32132132178",
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
                            responseCode = "201",
                            description = "Usuário criado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Usuário já existe",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppErrorResponse.class)
                            )
                    )
            }
    )
    @BadRequestResponse
    public ResponseEntity<Response> post(@RequestBody Request request) {
        LOGGER.info("Criando usuário: cpf={}, email={}",
                DataMasking.maskCpf(request.cpf),
                DataMasking.maskEmail(request.emailAddress)
        );
        var user = createUserUseCase.createUser(request.toUseCaseRequest());
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(user));
    }


    @Schema(name = "CreateUserRequest")
    public record Request(
            @Schema(description = "Nome do usuário", example = "Joao Silva")
            String name,
            @Schema(description = "CPF do usuário", example = "12312312387")
            String cpf,
            @Schema(description = "E-mail do usuário", example = "joao.silva@email.com")
            String emailAddress,
            @Schema(description = "Senha do usuário", example = "Senha!12345")
            String password
    ) {
        public CreateUserUseCase.Request toUseCaseRequest() {
            return new CreateUserUseCase.Request(
                    PersonName.of(name),
                    Cpf.of(cpf),
                    EmailAddress.of(emailAddress),
                    PlainTextPassword.of(password)
            );
        }
    }

    @Schema(name = "CreateUserResponse")
    public record Response(
            @Schema(description = "ID do usuário", example = "1")
            Long id,
            @Schema(description = "Nome do usuário", example = "Joao Silva")
            String name,
            @Schema(description = "CPF do usuário", example = "12312312387")
            String cpf,
            @Schema(description = "E-mail do usuário", example = "joao.silva@email.com")
            String emailAddress
    ) {
        public static Response of(User user) {
            return new Response(
                    user.id().value(),
                    user.name().value(),
                    user.cpf().value(),
                    user.emailAddress().value()
            );
        }
    }
}
