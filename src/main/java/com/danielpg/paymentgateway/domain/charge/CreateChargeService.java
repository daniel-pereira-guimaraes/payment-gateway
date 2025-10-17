package com.danielpg.paymentgateway.domain.charge;

import com.danielpg.paymentgateway.domain.AppClock;
import com.danielpg.paymentgateway.domain.user.Cpf;
import com.danielpg.paymentgateway.domain.user.User;
import com.danielpg.paymentgateway.domain.user.UserNotFoundException;
import com.danielpg.paymentgateway.domain.user.UserRepository;

public class CreateChargeService {

    private static final int DUE_IN_DAYS = 30;

    private final UserRepository userRepository;
    private final ChargeRepository chargeRepository;
    private final AppClock clock;

    public CreateChargeService(UserRepository userRepository,
                               ChargeRepository chargeRepository,
                               AppClock clock) {
        this.userRepository = userRepository;
        this.chargeRepository = chargeRepository;
        this.clock = clock;
    }


    public Charge createCharge(Request request) {
        var issuer = getUser(request.issuerCpf);
        var payer = getUser(request.payercpf);
        var charge = buildCharge(request, issuer, payer);
        chargeRepository.save(charge);
        return charge;
    }

    private Charge buildCharge(Request request, User issuer, User payer) {
        var now = clock.now();
        return Charge.builder()
                .withIssuerId(issuer.id())
                .withPayerId(payer.id())
                .withAmount(request.amount)
                .withDescription(request.description)
                .withCreatedAt(now)
                .withDueAt(now.plusDays(DUE_IN_DAYS))
                .build();
    }


    private User getUser(Cpf cpf) {
        return userRepository.get(cpf)
                .orElseThrow(() -> new UserNotFoundException(cpf));
    }

    public record Request(
            Cpf issuerCpf,
            Cpf payercpf,
            Amount amount,
            String description) {
    };
}
