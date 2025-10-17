package com.danielpg.paymentgateway.fixture;

import com.danielpg.paymentgateway.domain.user.*;

import java.math.BigDecimal;

public class UserFixture {

    public static final UserId USER_ID = UserId.of(123L);
    public static final Cpf CPF = Cpf.of("00000000191");
    public static final EmailAddress EMAIL_ADDRESS = EmailAddress.of("a@b.com");
    public static final PersonName PERSON_NAME = PersonName.of("DANIEL");
    public static final HashedPassword HASHED_PASSWORD = HashedPassword.of("hash");
    public static final Balance BALANCE = Balance.of(BigDecimal.TEN.add(BigDecimal.TWO));

    private UserFixture() {
    }

    public static User.Builder builder() {
        return User.builder()
                .withId(USER_ID)
                .withCpf(CPF)
                .withEmailAddress(EMAIL_ADDRESS)
                .withName(PERSON_NAME)
                .withHashedPassword(HASHED_PASSWORD)
                .withBalance(BALANCE);
    }

}
