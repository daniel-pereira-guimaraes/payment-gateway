package com.danielpg.paymentgateway.ut.domain.deposit;

import com.danielpg.paymentgateway.domain.deposit.Deposit;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.danielpg.paymentgateway.fixture.DepositFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DepositTest {

    @Test
    void buildsDepositWhenValid() {
        var deposit = builder().build();

        assertThat(deposit.id(), is(DEPOSIT_ID));
        assertThat(deposit.userId(), is(USER_ID));
        assertThat(deposit.amount(), is(AMOUNT));
        assertThat(deposit.createdAt(), is(CREATED_AT));
    }

    @Test
    void throwsExceptionWhenUserIsNull() {
        var builder = builder().withUserId(null);

        var ex = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(ex.getMessage(), is("O usuário é requerido."));
    }

    @Test
    void throwsExceptionWhenAmountIsNull() {
        var builder = builder().withAmount(null);

        var ex = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(ex.getMessage(), is("O valor é requerido."));
    }

    @Test
    void throwsExceptionWhenCreatedAtIsNull() {
        var builder = builder().withCreatedAt(null);

        var ex = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(ex.getMessage(), is("A data/hora do depósito é requerida;"));
    }

    @Test
    void setsIdWhenFinalizingCreation() {
        var deposit = Deposit.builder()
                .withUserId(USER_ID)
                .withAmount(AMOUNT)
                .build();

        deposit.finalizeCreation(DEPOSIT_ID);

        assertThat(deposit.id(), is(DEPOSIT_ID));
    }

    @Test
    void throwsExceptionWhenFinalizingCreationTwice() {
        var deposit = builder().build();

        var ex = assertThrows(IllegalStateException.class,
                () -> deposit.finalizeCreation(DEPOSIT_ID));

        assertThat(ex.getMessage(), is("A criação do depósito já foi finalizada."));
    }

    @Test
    void throwsExceptionWhenFinalizingCreationWithNullId() {
        var deposit = Deposit.builder()
                .withUserId(USER_ID)
                .withAmount(AMOUNT)
                .build();

        var ex = assertThrows(IllegalArgumentException.class,
                () -> deposit.finalizeCreation(null));

        assertThat(ex.getMessage(), is("O id é requerido."));
    }

    @Test
    void equalsAndHashCodeWorkWhenComparingDeposits() {
        var d1 = builder().build();
        var d2 = builder().build();
        var d3 = builder().withAmount(PositiveMoney.of(BigDecimal.ONE)).build();

        assertThat(d1, is(d2));
        assertThat(d1.hashCode(), is(d2.hashCode()));

        assertThat(d1, not(d3));
        assertThat(d1.hashCode(), not(d3.hashCode()));
    }
}
