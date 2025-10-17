package com.danielpg.paymentgateway.domain.user;

public interface PasswordHasher {
    String hash(PlainTextPassword plainTextPassword);
    boolean matches(PlainTextPassword plainTextPassword, HashedPassword hashedPassword);
}
