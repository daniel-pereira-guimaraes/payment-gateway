package com.danielpg.paymentgateway.fixture;

import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.charge.payment.Payment;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentId;

import static com.danielpg.paymentgateway.fixture.ChargeFixture.CHARGE_ID;

public class PaymentFixture {

    public static final PaymentId PAYMENT_ID = PaymentId.of(258L);
    public static final TimeMillis PAID_AT = TimeMillis.now();

    private PaymentFixture(){
    }

    public static Payment.Builder builder() {
        return Payment.builder()
                .withId(PAYMENT_ID)
                .withChargeId(CHARGE_ID)
                .withPaidAt(PAID_AT);
    }
}
