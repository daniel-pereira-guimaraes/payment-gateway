package com.danielpg.paymentgateway.fixture;

import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardCvv;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardExpirationDate;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardNumber;

public class CreditCardFixture {

    public static final CreditCardNumber CREDIT_CARD_NUMBER = CreditCardNumber.of("4111111111111111");
    public static final CreditCardCvv CREDIT_CARD_CVV = CreditCardCvv.of("123");
    public static final CreditCardExpirationDate CREDIT_CARD_EXPIRATION_DATE = CreditCardExpirationDate.of("12/2999");

    private CreditCardFixture() {
    }

    public static CreditCard.Builder builder() {
        return CreditCard.builder()
                .withNumber(CREDIT_CARD_NUMBER)
                .withCvv(CREDIT_CARD_CVV)
                .withExpirationDate(CREDIT_CARD_EXPIRATION_DATE);
    }

}


