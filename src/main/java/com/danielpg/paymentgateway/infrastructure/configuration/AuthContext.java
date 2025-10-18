package com.danielpg.paymentgateway.infrastructure.configuration;

import com.danielpg.paymentgateway.application.auth.AppTokenService;
import com.danielpg.paymentgateway.application.auth.LoginUseCase;
import com.danielpg.paymentgateway.domain.user.PasswordHasher;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthContext {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppTokenService tokenService;

    @Autowired
    private PasswordHasher passwordHasher;

    @Bean
    public LoginUseCase loginUseCase() {
        return new LoginUseCase(userRepository, tokenService, passwordHasher);
    }
}
