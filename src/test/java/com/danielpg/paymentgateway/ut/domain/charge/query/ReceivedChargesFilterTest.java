package com.danielpg.paymentgateway.ut.domain.charge.query;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesFilter;
import com.danielpg.paymentgateway.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReceivedChargesFilterTest {

    @Test
    void createsSuccessfullyWithStatuses() {
        var payerId = UserId.of(10L);
        var statuses = Set.of(ChargeStatus.PAID, ChargeStatus.PENDING);

        var filter = new ReceivedChargesFilter(payerId, statuses);

        assertThat(filter.payerId(), is(payerId));
        assertThat(filter.statuses(), is(statuses));
    }

    @Test
    void createsSuccessfullyWithoutStatuses() {
        var payerId = UserId.of(20L);

        var filter = new ReceivedChargesFilter(payerId, null);

        assertThat(filter.payerId(), is(payerId));
        assertThat(filter.statuses(), is((Set<ChargeStatus>) null));
    }

    @Test
    void throwsExceptionWhenPayerIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new ReceivedChargesFilter(null, Set.of(ChargeStatus.CANCELED)));

        assertThat(exception.getMessage(), is("O ID do pagamento é requerido."));
    }
}
