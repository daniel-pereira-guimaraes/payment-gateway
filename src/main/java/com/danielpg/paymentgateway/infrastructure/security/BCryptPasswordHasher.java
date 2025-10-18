package com.danielpg.paymentgateway.infrastructure.security;

import com.danielpg.paymentgateway.domain.user.HashedPassword;
import com.danielpg.paymentgateway.domain.user.PasswordHasher;
import com.danielpg.paymentgateway.domain.user.PlainTextPassword;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public HashedPassword hashedPassword(PlainTextPassword plainTextPassword) {
        return HashedPassword.of(encoder.encode(plainTextPassword.value()));
    }

    @Override
    public boolean matches(PlainTextPassword plainTextPassword, HashedPassword hashedPassword) {
        return encoder.matches(plainTextPassword.value(), hashedPassword.hash());
    }
}

