package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.shared.AppClock;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.user.UserRepository;

public class RegisterPaymentService {

    private final ChargeRepository chargeRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAuthorizer paymentAuthorizer;
    private final AppClock clock;

    public RegisterPaymentService(ChargeRepository chargeRepository,
                                  UserRepository userRepository,
                                  PaymentRepository paymentRepository,
                                  PaymentAuthorizer paymentAuthorizer,
                                  AppClock clock) {
        this.chargeRepository = chargeRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.paymentAuthorizer = paymentAuthorizer;
        this.clock = clock;
    }

    public Payment registerPayment(RegisterPaymentRequest request) {
        checkIfPaymentAlreadyExists(request.charge());

        request.charge().ensurePendingStatus();

        if (request.method() == PaymentMethod.CREDIT_CARD) {
            paymentAuthorizer.authorizePayment(request.charge());
        }

        var payment = buildPayment(request.charge(), request);

        if (request.method() == PaymentMethod.BALANCE) {
            updateBalances(request.charge());
        }

        request.charge().changeStatusToPaid();
        chargeRepository.save(request.charge());
        paymentRepository.save(payment);

        return payment;
    }

    private void updateBalances(Charge charge) {
        var issuer = userRepository.getOrThrow(charge.issuerId());
        var payer = userRepository.getOrThrow(charge.payerId());
        issuer.increaseBalance(charge.amount());
        payer.decreaseBalance(charge.amount());
        userRepository.save(issuer);
        userRepository.save(payer);
    }

    private void checkIfPaymentAlreadyExists(Charge charge) {
        if (paymentRepository.exists(charge.id())) {
            throw new IllegalStateException("Já existe um pagamento para esta cobrança: " + charge.id().value());
        }
    }

    private Payment buildPayment(Charge charge, RegisterPaymentRequest request) {
        return Payment.builder()
                .withChargeId(charge.id())
                .withMethod(request.method())
                .withCreditCard(request.creditCard())
                .withPaidAt(clock.now())
                .build();
    }
}
