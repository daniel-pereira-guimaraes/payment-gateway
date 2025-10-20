package com.danielpg.paymentgateway.domain.deposit;

import com.danielpg.paymentgateway.domain.charge.payment.PaymentAuthorizer;
import com.danielpg.paymentgateway.domain.shared.AppClock;
import com.danielpg.paymentgateway.domain.user.User;
import com.danielpg.paymentgateway.domain.user.UserRepository;

public class CreateDepositService {

    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final PaymentAuthorizer authorizer;
    private final AppClock clock;

    public CreateDepositService(DepositRepository depositRepository,
                                UserRepository userRepository,
                                PaymentAuthorizer authorizer,
                                AppClock clock) {
        this.depositRepository = depositRepository;
        this.userRepository = userRepository;
        this.authorizer = authorizer;
        this.clock = clock;
    }

    public Deposit createDeposit(DepositRequest request) {
        var user = userRepository.getOrThrow(request.userId());
        var deposit = buildDeposit(request);
        authorizer.authorizeDeposit(deposit);
        depositRepository.save(deposit);
        increaseBalance(request, user);
        return deposit;
    }

    private void increaseBalance(DepositRequest request, User user) {
        user.increaseBalance(request.amount());
        userRepository.save(user);
    }

    private Deposit buildDeposit(DepositRequest request) {
        return Deposit.builder()
                .withUserId(request.userId())
                .withAmount(request.amount())
                .withCreatedAt(clock.now())
                .build();
    }
}
