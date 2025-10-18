package com.danielpg.paymentgateway.ut.domain.user;

import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.user.*;
import com.danielpg.paymentgateway.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static com.danielpg.paymentgateway.fixture.UserFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @ParameterizedTest
    @CsvSource(value = {"null", "123"}, nullValues = {"null"})
    void createSuccessfully(Long idValue) {
        var id = UserId.ofNullable(idValue).orElse(null);
        var user = builder().withId(id).build();

        assertThat(user.id(), is(id));
        assertThat(user.name(), is(PERSON_NAME));
        assertThat(user.cpf(), is(CPF));
        assertThat(user.emailAddress(), is(EMAIL_ADDRESS));
        assertThat(user.hashedPassword(), is(HASHED_PASSWORD));
        assertThat(user.balance(), is(BALANCE));
    }

    @Test
    void throwsExceptionWhenCreatingWithNullName() {
        var builder = UserFixture.builder().withName(null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );

        assertThat(exception.getMessage(), is("O nome é requerido."));
    }

    @Test
    void throwsExceptionWhenCreatingWithNullEmailAddress() {
        var builder = UserFixture.builder().withEmailAddress(null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );

        assertThat(exception.getMessage(), is("O e-mail é requerido."));
    }

    @Test
    void throwsExceptionWhenCreatingWithNullHashedPassword() {
        var builder = UserFixture.builder().withHashedPassword(null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );

        assertThat(exception.getMessage(), is("A senha é requerida."));
    }

    @Test
    void throwsExceptionWhenCreatingWithNullBalance() {
        var builder = UserFixture.builder().withBalance(null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );

        assertThat(exception.getMessage(), is("O saldo é requerido."));
    }

    @Test
    void throwsExceptionWhenFinalizingWithNullId() {
        var userWithoutId = builder().withId(null).build();

        var exception = assertThrows(IllegalArgumentException.class,
                () -> userWithoutId.finalizeCreation(null)
        );

        assertThat(exception.getMessage(), is("O id é requerido."));
    }

    @Test
    void throwsExceptionWhenAlreadyFinalized() {
        var userWithId = builder().build();

        var exception = assertThrows(IllegalStateException.class,
                () -> userWithId.finalizeCreation(USER_ID)
        );

        assertThat(exception.getMessage(), is("A criação do usuário já foi finalizada."));
    }

    @Test
    void equalsAndHashCodeAreEqualForSameValues() {
        var user1 = builder().build();
        var user2 = builder().build();

        assertThat(user1, is(user2));
        assertThat(user1.hashCode(), is(user2.hashCode()));
    }

    @Test
    void equalsReturnsFalseForDifferentId() {
        var user1 = builder().withId(UserId.of(1L)).build();
        var user2 = builder().withId(UserId.of(2L)).build();

        assertThat(user1, not(user2));
    }

    @Test
    void equalsReturnsFalseForDifferentCpf() {
        var user1 = builder().withCpf(Cpf.of("00000000191")).build();
        var user2 = builder().withCpf(Cpf.of("99999999808")).build();

        assertThat(user1, not(user2));
    }

    @Test
    void equalsReturnsFalseForDifferentEmailAddress() {
        var user1 = builder().withEmailAddress(EmailAddress.of("a@b.com")).build();
        var user2 = builder().withEmailAddress(EmailAddress.of("c@d.com")).build();

        assertThat(user1, not(user2));
    }

    @Test
    void equalsReturnsFalseForDifferentName() {
        var user1 = builder().withName(PersonName.of("João")).build();
        var user2 = builder().withName(PersonName.of("Maria")).build();

        assertThat(user1, not(user2));
    }

    @Test
    void equalsReturnsFalseForDifferentHashedPassword() {
        var user1 = builder().withHashedPassword(HashedPassword.of("hash1")).build();
        var user2 = builder().withHashedPassword(HashedPassword.of("hash2")).build();

        assertThat(user1, not(user2));
    }

    @Test
    void increasesBalanceWhenValidAmount() {
        var user = builder().withBalance(Balance.of(BigDecimal.TEN)).build();

        user.increaseBalance(PositiveMoney.of(BigDecimal.TWO));

        assertThat(user.balance(), is(Balance.of(new BigDecimal(12))));
    }

    @Test
    void decreasesBalanceWhenAmountIsLessThanBalance() {
        var user = builder().withBalance(Balance.of(BigDecimal.TEN)).build();

        user.decreaseBalance(PositiveMoney.of(BigDecimal.TWO));

        assertThat(user.balance(), is(Balance.of(new BigDecimal("8"))));
    }

    @Test
    void decreasesBalanceWhenAmountEqualsBalance() {
        var user = builder().withBalance(Balance.of(BigDecimal.TEN)).build();

        user.decreaseBalance(PositiveMoney.of(BigDecimal.TEN));

        assertThat(user.balance(), is(Balance.of(BigDecimal.ZERO)));
    }

    @Test
    void throwsExceptionWhenDecreasingMoreThanBalance() {
        var user = builder().withBalance(Balance.of(BigDecimal.TEN)).build();

        var exception = assertThrows(InsufficientBalanceException.class,
                () -> user.decreaseBalance(PositiveMoney.of(new BigDecimal("10.01")))
        );

        assertThat(exception.getMessage(), is("Saldo insuficiente."));
    }



}

