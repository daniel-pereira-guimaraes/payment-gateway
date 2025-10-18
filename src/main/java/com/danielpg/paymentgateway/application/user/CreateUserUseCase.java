package com.danielpg.paymentgateway.application.user;

import com.danielpg.paymentgateway.application.shared.AppTransaction;
import com.danielpg.paymentgateway.domain.user.*;

public class CreateUserUseCase {

    private final AppTransaction transaction;
    private final UserRepository repository;
    private final PasswordHasher passwordHasher;

    public CreateUserUseCase(AppTransaction transaction,
                             UserRepository repository,
                             PasswordHasher passwordHasher) {
        this.transaction = transaction;
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    public User createUser(Request request) {
        var user = buildUser(request);
        transaction.execute(() -> repository.save(user));
        return user;
    }

    private User buildUser(Request request) {
        return User.builder()
                .withName(request.name)
                .withCpf(request.cpf)
                .withEmailAddress(request.emailAddress)
                .withHashedPassword(passwordHasher.hashedPassword(request.plainTextPassword))
                .build();
    }

    public record Request(
            PersonName name,
            Cpf cpf,
            EmailAddress emailAddress,
            PlainTextPassword plainTextPassword) {
    }
}
