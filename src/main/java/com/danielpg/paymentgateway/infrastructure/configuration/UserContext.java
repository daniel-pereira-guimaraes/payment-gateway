package com.danielpg.paymentgateway.infrastructure.configuration;

import com.danielpg.paymentgateway.application.shared.AppTransaction;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.application.user.CreateUserUseCase;
import com.danielpg.paymentgateway.application.user.GetCurrentUserUseCase;
import com.danielpg.paymentgateway.domain.user.PasswordHasher;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserContext {

    @Autowired
    private AppTransaction appTransaction;

    @Autowired
    private PasswordHasher passwordHasher;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequesterProvider requesterProvider;

    @Bean
    public CreateUserUseCase createUserUseCase() {
        return new CreateUserUseCase(appTransaction, userRepository, passwordHasher);
    }

    @Bean
    public GetCurrentUserUseCase getCurrentUserUseCase() {
        return new GetCurrentUserUseCase(requesterProvider, userRepository);
    }

}