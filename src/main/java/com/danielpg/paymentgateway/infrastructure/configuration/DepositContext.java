package com.danielpg.paymentgateway.infrastructure.configuration;

import com.danielpg.paymentgateway.application.deposit.CreateDepositUseCase;
import com.danielpg.paymentgateway.application.shared.AppTransaction;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentAuthorizer;
import com.danielpg.paymentgateway.domain.deposit.CreateDepositService;
import com.danielpg.paymentgateway.domain.deposit.DepositRepository;
import com.danielpg.paymentgateway.domain.shared.AppClock;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepositContext {

    @Autowired
    private AppClock clock;

    @Autowired
    private AppTransaction transaction;

    @Autowired
    private RequesterProvider requesterProvider;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentAuthorizer paymentAuthorizer;

    @Bean
    public CreateDepositService createDepositService() {
        return new CreateDepositService(depositRepository, userRepository, paymentAuthorizer, clock);
    }

    @Bean
    public CreateDepositUseCase createDepositUseCase(CreateDepositService createDepositService) {
        return new CreateDepositUseCase(transaction, requesterProvider, createDepositService);
    }

}
