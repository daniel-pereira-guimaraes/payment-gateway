package com.danielpg.paymentgateway.domain.charge;

import com.danielpg.paymentgateway.domain.charge.payment.PaymentAuthorizer;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentRepository;
import com.danielpg.paymentgateway.domain.user.UserRepository;

public class CancelChargeService {

    private final ChargeRepository chargeRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAuthorizer paymentAuthorizer;

    public CancelChargeService(ChargeRepository chargeRepository,
                               UserRepository userRepository,
                               PaymentRepository paymentRepository,
                               PaymentAuthorizer paymentAuthorizer) {
        this.chargeRepository = chargeRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.paymentAuthorizer = paymentAuthorizer;
    }

    public void cancelCharge(Charge charge) {
        switch (charge.status()) {
            case PENDING -> cancelPendingCharge(charge);
            case PAID -> cancelPaidCharge(charge);
            default -> throw new IllegalStateException(
                    "A cobrança não pode ser cancelada no status atual: " + charge.status());
        }

        chargeRepository.save(charge);
    }

    private void cancelPendingCharge(Charge charge) {
        charge.changeStatusToCanceled();
    }

    private void cancelPaidCharge(Charge charge) {
        var paymentOpt = paymentRepository.get(charge.id());

        if (paymentOpt.isPresent()) {
            var payment = paymentOpt.get();

            switch (payment.method()) {
                case BALANCE -> refundBalance(charge);
                case CREDIT_CARD -> paymentAuthorizer.authorizeCancellation(charge, payment.creditCard());
                default -> throw new IllegalStateException("Método de pagamento desconhecido: " + payment.method());
            }
        }

        charge.changeStatusToCanceled();
    }

    private void refundBalance(Charge charge) {
        var issuer = userRepository.getOrThrow(charge.issuerId());
        var payer = userRepository.getOrThrow(charge.payerId());

        issuer.decreaseBalance(charge.amount());
        payer.increaseBalance(charge.amount());

        userRepository.save(issuer);
        userRepository.save(payer);
    }
}
