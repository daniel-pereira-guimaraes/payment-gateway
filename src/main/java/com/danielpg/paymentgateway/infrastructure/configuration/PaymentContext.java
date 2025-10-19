package com.danielpg.paymentgateway.infrastructure.configuration;

import com.danielpg.paymentgateway.application.charge.payment.RegisterPaymentUseCase;
import com.danielpg.paymentgateway.application.shared.AppTransaction;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentAuthorizer;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentRepository;
import com.danielpg.paymentgateway.domain.charge.payment.RegisterPaymentService;
import com.danielpg.paymentgateway.domain.shared.AppClock;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentContext {

    @Autowired
    private AppClock clock;

    @Autowired
    private AppTransaction transaction;

    @Autowired
    private RequesterProvider requesterProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentAuthorizer paymentAuthorizer;

    @Bean
    public RegisterPaymentService registerPaymentService() {
        return new RegisterPaymentService(chargeRepository, userRepository,
                paymentRepository, paymentAuthorizer, clock);
    }

    @Bean
    public RegisterPaymentUseCase registerPaymentUseCase(RegisterPaymentService registerPaymentService) {
        return new RegisterPaymentUseCase(transaction, chargeRepository, requesterProvider, registerPaymentService);
    }

}
