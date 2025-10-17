package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.AppClock;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;

public class RegisterPaymentService {

    private final ChargeRepository chargeRepository;
    private final PaymentRepository paymentRepository;
    private final AppClock clock;

    public RegisterPaymentService(ChargeRepository chargeRepository,
                                  PaymentRepository paymentRepository,
                                  AppClock clock) {
        this.chargeRepository = chargeRepository;
        this.paymentRepository = paymentRepository;
        this.clock = clock;
    }

    public Payment registerPayment(ChargeId chargeId) {
        checkIfPaymentAlreadyExists(chargeId);
        var charge = getCharge(chargeId);
        var payment = buildPayment(charge);
        paymentRepository.save(payment);
        return payment;
    }

    private void checkIfPaymentAlreadyExists(ChargeId chargeId) {
        if (paymentRepository.exists(chargeId)) {
            throw new IllegalStateException("Já existe um pagamento para esta cobrança: " + chargeId.value());
        }
    }

    private Charge getCharge(ChargeId chargeId) {
        return chargeRepository.getOrThrow(chargeId);
    }

    private Payment buildPayment(Charge charge) {
        return Payment.builder()
                .withChargeId(charge.id())
                .withPaidAt(clock.now())
                .build();
    }

}
