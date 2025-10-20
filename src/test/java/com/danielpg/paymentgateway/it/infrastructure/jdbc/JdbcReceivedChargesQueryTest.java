package com.danielpg.paymentgateway.it.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesFilter;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesItem;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesItem.Issuer;
import com.danielpg.paymentgateway.domain.user.UserId;
import com.danielpg.paymentgateway.infrastructure.jdbc.JdbcReceivedChargesQuery;
import com.danielpg.paymentgateway.it.infrastructure.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JdbcReceivedChargesQueryTest extends IntegrationTestBase {

    private static final List<ReceivedChargesItem> EXPECTED_ALL_CHARGES = List.of(
            new ReceivedChargesItem(
                    3L,
                    new ReceivedChargesItem.Issuer("32132132178", "Maria Oliveira"),
                    new BigDecimal("500.00"),
                    "Assinatura mensal",
                    1700014400L,
                    1700018000L,
                    ChargeStatus.CANCELED,
                    null
            ),
            new ReceivedChargesItem(
                    4L,
                    new ReceivedChargesItem.Issuer("32132132178", "Maria Oliveira"),
                    new BigDecimal("200.00"),
                    "Compra de escritorio",
                    1700003601L,
                    1700007201L,
                    ChargeStatus.PENDING,
                    null
            ),
            new ReceivedChargesItem(
                    6L,
                    new ReceivedChargesItem.Issuer("32132132178", "Maria Oliveira"),
                    new BigDecimal("1000.00"),
                    "Projeto especial",
                    1700018001L,
                    1700021601L,
                    ChargeStatus.CANCELED,
                    null
            ),
            new ReceivedChargesItem(
                    8L,
                    new ReceivedChargesItem.Issuer("32132132178", "Maria Oliveira"),
                    new BigDecimal("1000.00"),
                    "Servico de TI",
                    1700018003L,
                    1700021603L,
                    ChargeStatus.PAID,
                    1700014800L
            ),
            new ReceivedChargesItem(
                    9L,
                    new ReceivedChargesItem.Issuer("32132132178", "Maria Oliveira"),
                    new BigDecimal("1000.01"),
                    "Valor alto",
                    1700018009L,
                    1700021609L,
                    ChargeStatus.PENDING,
                    null
            )
    );

    private static final List<ReceivedChargesItem> EXPECTED_PAID_CHARGES = List.of(
            new ReceivedChargesItem(
                    8L,
                    new ReceivedChargesItem.Issuer("32132132178", "Maria Oliveira"),
                    new BigDecimal("1000.00"),
                    "Servico de TI",
                    1700018003L,
                    1700021603L,
                    ChargeStatus.PAID,
                    1700014800L
            )
    );

    private static final List<ReceivedChargesItem> EXPECTED_CANCELED_CHARGES = List.of(
            new ReceivedChargesItem(
                    7L,
                    new ReceivedChargesItem.Issuer("12312312387", "Joao Silva"),
                    new BigDecimal("300.00"),
                    "Servico de jardinagem",
                    1700010802L,
                    1700014402L,
                    ChargeStatus.CANCELED,
                    null
            )
    );

    @Autowired
    private JdbcReceivedChargesQuery repository;

    @Test
    void returnsAllChargesWhenStatusesIsNull() {
        var filter = new ReceivedChargesFilter(UserId.of(1L), null);

        var result = repository.execute(filter);

        System.out.println(result);
        assertThat(result, is(EXPECTED_ALL_CHARGES));
    }

    @Test
    void returnsAllChargesWhenStatusesIsEmpty() {
        var filter = new ReceivedChargesFilter(UserId.of(1L), Set.of());

        var result = repository.execute(filter);

        assertThat(result, is(EXPECTED_ALL_CHARGES));
    }

    @Test
    void returnsPaidChargesWhenStatusesIsPaid() {
        var filter = new ReceivedChargesFilter(UserId.of(1L), Set.of(ChargeStatus.PAID));

        var result = repository.execute(filter);

        System.out.println(result);
        assertThat(result, is(EXPECTED_PAID_CHARGES));
    }

    @Test
    void returnsCanceledChargesWhenStatusesIsCanceled() {
        var filter = new ReceivedChargesFilter(UserId.of(2L), Set.of(ChargeStatus.CANCELED));

        var result = repository.execute(filter);

        System.out.println(result);
        assertThat(result, is(EXPECTED_CANCELED_CHARGES));
    }
}
