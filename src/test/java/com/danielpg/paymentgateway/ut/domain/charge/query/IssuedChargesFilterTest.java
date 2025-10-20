package com.danielpg.paymentgateway.ut.domain.charge.query;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesFilter;
import com.danielpg.paymentgateway.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IssuedChargesFilterTest {

    @Test
    void createsSuccessfullyWithStatuses() {
        var issuerId = UserId.of(10L);
        var statuses = Set.of(ChargeStatus.PAID, ChargeStatus.PENDING);

        var filter = new IssuedChargesFilter(issuerId, statuses);

        assertThat(filter.issuerId(), is(issuerId));
        assertThat(filter.statuses(), is(statuses));
    }

    @Test
    void createsSuccessfullyWithoutStatuses() {
        var issuerId = UserId.of(20L);

        var filter = new IssuedChargesFilter(issuerId, null);

        assertThat(filter.issuerId(), is(issuerId));
        assertThat(filter.statuses(), is((Set<ChargeStatus>) null));
    }

    @Test
    void throwsExceptionWhenIssuerIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new IssuedChargesFilter(null, Set.of(ChargeStatus.CANCELED)));

        assertThat(exception.getMessage(), is("O ID do emitente é requerido."));
    }
}
