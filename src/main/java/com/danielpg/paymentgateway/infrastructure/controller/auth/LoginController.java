package com.danielpg.paymentgateway.infrastructure.controller.auth;

import com.danielpg.paymentgateway.application.auth.LoginUseCase;
import com.danielpg.paymentgateway.application.auth.Token;
import com.danielpg.paymentgateway.domain.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<Response> post(@RequestBody Request request) {
        var token = loginUseCase.login(request.toUseCaseRequest());

        return ResponseEntity.status(HttpStatus.OK).body(Response.of(token));
    }

    public record Request(
            String emailAddress,
            String cpf,
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

    public record Response(String token) {
        public static Response of(Token token) {
            return new Response(token.rawToken());
        }
    }
}
