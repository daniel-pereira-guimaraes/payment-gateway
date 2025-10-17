package com.danielpg.paymentgateway.fixture;

import com.danielpg.paymentgateway.domain.charge.Amount;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.user.UserId;

import java.math.BigDecimal;

public class ChargeFixture {

    public static final ChargeId CHARGE_ID = ChargeId.of(321L);
    public static final UserId ISSUER_ID = UserId.of(456L);
    public static final UserId PAYER_ID = UserId.of(789L);
    public static final Amount AMOUNT = Amount.of(BigDecimal.TEN);
    public static final String DESCRIPTION = "Charge description";

    private ChargeFixture() {
    }

    public static Charge.Builder builder() {
        return Charge.builder()
                .withId(CHARGE_ID)
                .withIssuerId(ISSUER_ID)
                .withPayerId(PAYER_ID)
                .withAmount(AMOUNT)
                .withDescription(DESCRIPTION);
    }
}
