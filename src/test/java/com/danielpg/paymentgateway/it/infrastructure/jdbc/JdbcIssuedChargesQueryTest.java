package com.danielpg.paymentgateway.it.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesFilter;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesItem;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesItem.Payer;
import com.danielpg.paymentgateway.domain.user.UserId;
import com.danielpg.paymentgateway.infrastructure.jdbc.JdbcIssuedChargesQuery;
import com.danielpg.paymentgateway.it.infrastructure.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
class JdbcIssuedChargesQueryTest extends IntegrationTestBase {

    private static final List<IssuedChargesItem> EXPECTED_ALL_CHARGES = List.of(
            new IssuedChargesItem(
                    1L,
                    new Payer("32132132178", "Maria Oliveira"),
                    new BigDecimal("150.00"),
                    "Servico de limpeza",
                    1700000000L,
                    1700003600L,
                    ChargeStatus.PENDING,
                    null
            ),
            new IssuedChargesItem(
                    2L,
                    new Payer("32132132178", "Maria Oliveira"),
                    new BigDecimal("250.50"),
                    "Compra de material",
                    1700007200L,
                    1700010800L,
                    ChargeStatus.PAID,
                    1700007300L
            ),
            new IssuedChargesItem(
                    5L,
                    new Payer("32132132178", "Maria Oliveira"),
                    new BigDecimal("300.00"),
                    "Servico de manutencao",
                    1700010801L,
                    1700014401L,
                    ChargeStatus.PAID,
                    1700014500L
            ),
            new IssuedChargesItem(
                    7L,
                    new Payer("32132132178", "Maria Oliveira"),
                    new BigDecimal("300.00"),
                    "Servico de jardinagem",
                    1700010802L,
                    1700014402L,
                    ChargeStatus.CANCELED,
                    null
            )
    );

    private static final List<IssuedChargesItem> EXPECTED_PAID_CHARGES = List.of(
            new IssuedChargesItem(
                    2L,
                    new Payer("32132132178", "Maria Oliveira"),
                    new BigDecimal("250.50"),
                    "Compra de material",
                    1700007200L,
                    1700010800L,
                    ChargeStatus.PAID,
                    1700007300L
            ),
            new IssuedChargesItem(
                    5L,
                    new Payer("32132132178", "Maria Oliveira"),
                    new BigDecimal("300.00"),
                    "Servico de manutencao",
                    1700010801L,
                    1700014401L,
                    ChargeStatus.PAID,
                    1700014500L
            )
    );

    private static final List<IssuedChargesItem> EXPECTED_CANCELED_CHARGES = List.of(
            new IssuedChargesItem(
                    3L,
                    new Payer("12312312387", "Joao Silva"),
                    new BigDecimal("500.00"),
                    "Assinatura mensal",
                    1700014400L,
                    1700018000L,
                    ChargeStatus.CANCELED,
                    null
            ),
            new IssuedChargesItem(
                    6L,
                    new Payer("12312312387", "Joao Silva"),
                    new BigDecimal("1000.00"),
                    "Projeto especial",
                    1700018001L,
                    1700021601L,
                    ChargeStatus.CANCELED,
                    null
            )
    );

    @Autowired
    private JdbcIssuedChargesQuery repository;

    @Test
    void returnsAllChargesWhenStatusesIsNull() {
        var filter = new IssuedChargesFilter(UserId.of(1L), null);

        var result = repository.execute(filter);

        assertThat(result, is(EXPECTED_ALL_CHARGES));
    }

    @Test
    void returnsAllChargesWhenStatusesIsEmpty() {
        var filter = new IssuedChargesFilter(UserId.of(1L), Set.of());

        var result = repository.execute(filter);

        assertThat(result, is(EXPECTED_ALL_CHARGES));
    }

    @Test
    void returnsPaidChargesWhenStatusesIsPaid() {
        var filter = new IssuedChargesFilter(UserId.of(1L), Set.of(ChargeStatus.PAID));

        var result = repository.execute(filter);

        assertThat(result, is(EXPECTED_PAID_CHARGES));
    }

    @Test
    void returnsCanceledChargesWhenStatusesIsCanceled() {
        var filter = new IssuedChargesFilter(UserId.of(2L), Set.of(ChargeStatus.CANCELED));

        var result = repository.execute(filter);

        assertThat(result, is(EXPECTED_CANCELED_CHARGES));
    }

}
