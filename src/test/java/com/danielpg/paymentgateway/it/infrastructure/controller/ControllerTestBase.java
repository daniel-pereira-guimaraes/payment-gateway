package com.danielpg.paymentgateway.it.infrastructure.controller;


import com.danielpg.paymentgateway.application.auth.AppTokenService;
import com.danielpg.paymentgateway.domain.user.*;
import com.danielpg.paymentgateway.fixture.UserFixture;
import com.danielpg.paymentgateway.it.infrastructure.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.token.TokenService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

@AutoConfigureMockMvc
public abstract class ControllerTestBase extends IntegrationTestBase {

    protected static final User CURRENT_USER = User.builder()
            .withId(UserId.of(1L))
            .withName(PersonName.of("Joao Silva"))
            .withCpf(Cpf.of("12312312387"))
            .withEmailAddress(EmailAddress.of("joao.silva@email.com"))
            .withHashedPassword(HashedPassword.of("$2a$10$4LNeRTVaYHFqMEcpK6wV8.DPpiuNpT4DjXGzQnCZB/bBX6u1s9F3y"))
            .withBalance(Balance.of(new BigDecimal("1000.00")))
            .build();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    AppTokenService tokenService;

    protected String userToken() {
        return "Bearer " + tokenService.generate(CURRENT_USER).rawToken();
    }

}
