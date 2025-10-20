package com.danielpg.paymentgateway.fixture;

import com.danielpg.paymentgateway.domain.deposit.Deposit;
import com.danielpg.paymentgateway.domain.deposit.DepositId;
import com.danielpg.paymentgateway.domain.deposit.DepositRequest;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.user.UserId;

import java.math.BigDecimal;

public class DepositFixture {

    public static final DepositId DEPOSIT_ID = DepositId.of(1L);
    public static final UserId USER_ID = UserId.of(1L);
    public static final PositiveMoney AMOUNT = PositiveMoney.of(BigDecimal.TEN);
    public static final TimeMillis CREATED_AT = TimeMillis.of(1L);

    private DepositFixture() {
    }

    public static Deposit.Builder builder() {
        return Deposit.builder()
                .withId(DEPOSIT_ID)
                .withUserId(USER_ID)
                .withAmount(AMOUNT)
                .withCreatedAt(CREATED_AT);
    }

    public static DepositRequest aDepositRequest() {
        return DepositRequest.of(USER_ID, AMOUNT);
    }

}
