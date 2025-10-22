package com.danielpg.paymentgateway.application.auth;

import com.danielpg.paymentgateway.domain.shared.DataMasking;
import com.danielpg.paymentgateway.domain.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginUseCase.class);

    private final UserRepository userRepository;
    private final AppTokenService tokenService;
    private final PasswordHasher passwordHasher;

    public LoginUseCase(UserRepository userRepository,
                        AppTokenService tokenService, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordHasher = passwordHasher;
    }

    public Token login(Request request) {
        try {
            return tryLogin(request);
        } catch (IllegalArgumentException | InvalidCredentialsException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new InvalidCredentialsException();
        }
    }

    private Token tryLogin(Request request) {
        if (request.cpf != null) {
            return tryLoginByCpf(request);
        } else if (request.emailAddress != null) {
            return tryLoginByEmail(request);
        }
        throw new IllegalArgumentException("Obrigatório informar CPF ou e-mail.");
    }

    private Token tryLoginByCpf(Request request) {
        try {
            var user = userRepository.getOrThrow(request.cpf);
            return tryAuthenticate(user, request.plainTextPassword);
        } catch (RuntimeException e) {
            LOGGER.info("Erro ao fazer login: cpf={}, message={}",
                    DataMasking.maskCpf(request.cpf.value()),
                    e.getMessage()
            );
            throw e;
        }
    }

    private Token tryLoginByEmail(Request request) {
        try {
            var user = userRepository.getOrThrow(request.emailAddress);
            return tryAuthenticate(user, request.plainTextPassword);
        } catch (RuntimeException e) {
            LOGGER.info("Erro ao fazer login: emailAddress={}, message={}",
                    DataMasking.maskEmail(request.emailAddress.value()),
                    e.getMessage()
            );
            throw e;
        }
    }

    private Token tryAuthenticate(User user, PlainTextPassword plainTextPassword) {
        if (passwordHasher.matches(plainTextPassword, user.hashedPassword())) {
            return tokenService.generate(user);
        }
        throw new InvalidCredentialsException();
    }

    public record Request(
            EmailAddress emailAddress,
            Cpf cpf,
            PlainTextPassword plainTextPassword) {
    }

}
