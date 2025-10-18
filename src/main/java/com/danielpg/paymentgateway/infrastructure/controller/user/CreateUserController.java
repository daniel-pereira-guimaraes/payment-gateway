package com.danielpg.paymentgateway.infrastructure.controller.user;

import com.danielpg.paymentgateway.application.user.CreateUserUseCase;
import com.danielpg.paymentgateway.domain.shared.DataMasking;
import com.danielpg.paymentgateway.domain.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class CreateUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserController.class);

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @PostMapping
    public ResponseEntity<Response> post(@RequestBody Request request) {
        LOGGER.info("Criando usuário: email={}", DataMasking.maskEmail(request.emailAddress));
        var user = createUserUseCase.createUser(request.toUseCaseRequest());
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(user));
    }

    public record Request(
            String name,
            String cpf,
            String emailAddress,
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

    public record Response(
            Long id,
            String name,
            String cpf,
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
