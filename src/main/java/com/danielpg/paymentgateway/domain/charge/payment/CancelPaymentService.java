package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.user.UserRepository;

public class CancelPaymentService {

    private final ChargeRepository chargeRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAuthorizer paymentAuthorizer;

    public CancelPaymentService(ChargeRepository chargeRepository,
                                UserRepository userRepository,
                                PaymentRepository paymentRepository,
                                PaymentAuthorizer paymentAuthorizer) {
        this.chargeRepository = chargeRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.paymentAuthorizer = paymentAuthorizer;
    }

    public void cancelPayment(PaymentId paymentId) {
        var payment = paymentRepository.getOrThrow(paymentId);
        var charge = chargeRepository.getOrThrow(payment.chargeId());

        switch (charge.status()) {
            case PENDING -> cancelPendingCharge(charge);
            case PAID -> cancelPaidCharge(charge, payment);
            default -> throw new IllegalStateException(
                    "A cobrança não pode ser cancelada no status atual: " + charge.status());
        }

        chargeRepository.save(charge);
    }

    private void cancelPendingCharge(Charge charge) {
        charge.changeStatusToCanceled();
    }

    private void cancelPaidCharge(Charge charge, Payment payment) {
        if (payment.method() == PaymentMethod.BALANCE) {
            refundBalance(charge);
        } else if (payment.method() == PaymentMethod.CREDIT_CARD) {
            paymentAuthorizer.authorizeCancellation(charge, payment.creditCard());
        }
        charge.changeStatusToCanceled();
    }

    private void refundBalance(Charge charge) {
        var issuer = userRepository.getOrThrow(charge.issuerId());
        var payer = userRepository.getOrThrow(charge.payerId());

        payer.increaseBalance(charge.amount());
        issuer.decreaseBalance(charge.amount());

        userRepository.save(issuer);
        userRepository.save(payer);
    }
}
