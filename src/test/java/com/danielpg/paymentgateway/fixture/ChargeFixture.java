package com.danielpg.paymentgateway.fixture;

import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.user.UserId;

import java.math.BigDecimal;

public class ChargeFixture {

    public static final ChargeId CHARGE_ID = ChargeId.of(1L);
    public static final UserId ISSUER_ID = UserId.of(1L);
    public static final UserId PAYER_ID = UserId.of(2L);
    public static final PositiveMoney AMOUNT = PositiveMoney.of(BigDecimal.TEN);
    public static final String DESCRIPTION = "Charge description";
    public static final TimeMillis CREATED_AT = TimeMillis.of(1L);
    public static final TimeMillis DUE_AT = TimeMillis.of(2L);

    private ChargeFixture() {
    }

    public static Charge.Builder builder() {
        return Charge.builder()
                .withId(CHARGE_ID)
                .withIssuerId(ISSUER_ID)
                .withPayerId(PAYER_ID)
                .withAmount(AMOUNT)
                .withDescription(DESCRIPTION)
                .withCreatedAt(CREATED_AT)
                .withDueAt(DUE_AT)
                .withStatus(ChargeStatus.PENDING);
    }
}
