package com.danielpg.paymentgateway.ut.domain.deposit;

import com.danielpg.paymentgateway.domain.deposit.DepositRequest;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DepositRequestTest {

    @Test
    void createsRequestWhenValid() {
        var userId = UserId.of(1L);
        var amount = PositiveMoney.of(BigDecimal.TEN);

        var request = DepositRequest.of(userId, amount);

        assertThat(request.userId(), is(userId));
        assertThat(request.amount(), is(amount));
    }

    @Test
    void throwsExceptionWhenUserIdIsNull() {
        var amount = PositiveMoney.of(BigDecimal.TEN);

        var ex = assertThrows(NullPointerException.class, () ->
                DepositRequest.of(null, amount)
        );

        assertThat(ex.getMessage(), is("O usuário é requerido."));
    }

    @Test
    void throwsExceptionWhenAmountIsNull() {
        var userId = UserId.of(1L);

        var ex = assertThrows(NullPointerException.class, () ->
                DepositRequest.of(userId, null)
        );

        assertThat(ex.getMessage(), is("O valor é requerido."));
    }

    @Test
    void equalsAndHashCodeWorkProperly() {
        var userId = UserId.of(1L);
        var amount = PositiveMoney.of(BigDecimal.TEN);

        var r1 = DepositRequest.of(userId, amount);
        var r2 = DepositRequest.of(userId, amount);
        var r3 = DepositRequest.of(userId, PositiveMoney.of(BigDecimal.ONE));

        assertThat(r1, is(r2));
        assertThat(r1.hashCode(), is(r2.hashCode()));
        assertThat(r1, not(r3));
        assertThat(r1.hashCode(), not(r3.hashCode()));
    }
}
