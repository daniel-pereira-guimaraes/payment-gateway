package com.danielpg.paymentgateway.infrastructure.configuration;

import com.danielpg.paymentgateway.application.charge.CreateChargeUseCase;
import com.danielpg.paymentgateway.application.charge.FindIssuedChargesUseCase;
import com.danielpg.paymentgateway.application.shared.AppTransaction;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.charge.CreateChargeService;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesQuery;
import com.danielpg.paymentgateway.domain.shared.AppClock;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChargeContext {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private AppClock clock;

    @Autowired
    private AppTransaction transaction;

    @Autowired
    private RequesterProvider requesterProvider;

    @Bean
    public CreateChargeService createChargeService() {
        return new CreateChargeService(userRepository, chargeRepository, clock);
    }

    @Bean
    public CreateChargeUseCase createChargeUseCase(CreateChargeService createChargeService) {
        return new CreateChargeUseCase(transaction, requesterProvider, createChargeService);
    }

    @Bean
    public FindIssuedChargesUseCase findIssuedChargesUseCase(IssuedChargesQuery issuedChargesQuery) {
        return new FindIssuedChargesUseCase(requesterProvider,  issuedChargesQuery);
    }
}
