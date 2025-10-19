package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.ChargeId;
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
        checkIfPaymentAlreadyExists(request.chargeId());
        var charge = getCharge(request.chargeId());

        charge.ensurePendingStatus();

        if (request.method() == PaymentMethod.CREDIT_CARD) {
            paymentAuthorizer.authorizePayment(charge);
        }

        var payment = buildPayment(charge, request);

        if (request.method() == PaymentMethod.BALANCE) {
            updateBalances(charge);
        }

        charge.changeStatusToPaid();
        chargeRepository.save(charge);
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

    private void checkIfPaymentAlreadyExists(ChargeId chargeId) {
        if (paymentRepository.exists(chargeId)) {
            throw new IllegalStateException("Já existe um pagamento para esta cobrança: " + chargeId.value());
        }
    }

    private Charge getCharge(ChargeId chargeId) {
        return chargeRepository.getOrThrow(chargeId);
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
