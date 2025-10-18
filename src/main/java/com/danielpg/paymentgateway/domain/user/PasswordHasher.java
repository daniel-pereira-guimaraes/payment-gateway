package com.danielpg.paymentgateway.domain.user;

public interface PasswordHasher {
    HashedPassword hashedPassword(PlainTextPassword plainTextPassword);
    boolean matches(PlainTextPassword plainTextPassword, HashedPassword hashedPassword);
}
