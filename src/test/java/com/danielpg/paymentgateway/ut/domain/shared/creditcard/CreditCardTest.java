package com.danielpg.paymentgateway.ut.domain.shared.creditcard;

import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardCvv;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardExpirationDate;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardNumber;
import com.danielpg.paymentgateway.fixture.CreditCardFixture;
import org.junit.jupiter.api.Test;

import static com.danielpg.paymentgateway.fixture.CreditCardFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditCardTest {

    @Test
    void buildsSuccessfullyWhenAllFieldsAreProvided() {
        var card = builder().build();

        assertThat(card, notNullValue());
        assertThat(card.number(), is(CREDIT_CARD_NUMBER));
        assertThat(card.expirationDate(), is(CREDIT_CARD_EXPIRATION_DATE));
        assertThat(card.cvv(), is(CREDIT_CARD_CVV));
    }

    @Test
    void throwsExceptionWhenNumberIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> builder().withNumber(null).build());

        assertThat(exception.getMessage(), is("O número do cartão é requerido."));
    }

    @Test
    void throwsExceptionWhenExpirationDateIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> builder().withExpirationDate(null).build());

        assertThat(exception.getMessage(), is("A data de expiração do cartão é requerida."));
    }

    @Test
    void throwsExceptionWhenCvvIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> builder().withCvv(null).build());

        assertThat(exception.getMessage(), is("O cvv do cartão é requerido."));
    }

    @Test
    void equalsReturnsTrueForSameObject() {
        var card = builder().build();

        assertThat(card, is(card));
    }

    @Test
    void equalsReturnsTrueForEquivalentObjects() {
        var card1 = builder().build();
        var card2 = builder().build();

        assertThat(card1.equals(card2), is(true));
        assertThat(card2.equals(card1), is(true));
    }

    @Test
    void equalsReturnsFalseForDifferentNumber() {
        var card1 = builder().build();
        var card2 = builder().withNumber(CreditCardNumber.of("4111111111111112")).build();

        assertThat(card1.equals(card2), is(false));
    }

    @Test
    void equalsReturnsFalseForDifferentExpirationDate() {
        var card1 = builder().build();
        var card2 = builder().withExpirationDate(CreditCardExpirationDate.of("01/2999")).build();

        assertThat(card1.equals(card2), is(false));
    }

    @Test
    void equalsReturnsFalseForDifferentCvv() {
        var card1 = builder().build();
        var card2 = builder().withCvv(CreditCardCvv.of("999")).build();

        assertThat(card1.equals(card2), is(false));
    }

    @Test
    void hashCodeIsSameForEquivalentObjects() {
        var card1 = builder().build();
        var card2 = builder().build();

        assertThat(card1.hashCode(), is(card2.hashCode()));
    }

    @Test
    void hashCodeIsDifferentForDifferentObjects() {
        var card1 = builder().build();
        var card2 = builder().withCvv(CreditCardCvv.of("999")).build();

        assertThat(card1.hashCode(), not(card2.hashCode()));
    }

    @Test
    void ofNullableReturnsOptionalEmptyForNull() {
        var number = CreditCardNumber.ofNullable(null);
        var cvv = CreditCardCvv.ofNullable(null);
        var exp = CreditCardExpirationDate.ofNullable(null);

        assertThat(number.isEmpty(), is(true));
        assertThat(cvv.isEmpty(), is(true));
        assertThat(exp.isEmpty(), is(true));
    }

    @Test
    void ofNullableReturnsOptionalPresentForValidValue() {
        var number = CreditCardNumber.ofNullable(CREDIT_CARD_NUMBER.value());
        var cvv = CreditCardCvv.ofNullable(CREDIT_CARD_CVV.value());
        var exp = CreditCardExpirationDate.ofNullable(CREDIT_CARD_EXPIRATION_DATE.value());

        assertThat(number.isPresent(), is(true));
        assertThat(cvv.isPresent(), is(true));
        assertThat(exp.isPresent(), is(true));
    }

    private static CreditCard.Builder builder() {
        return CreditCardFixture.builder();
    }
}
