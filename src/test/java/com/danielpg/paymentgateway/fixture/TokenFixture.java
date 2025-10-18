package com.danielpg.paymentgateway.fixture;

import com.danielpg.paymentgateway.application.auth.Token;
import com.danielpg.paymentgateway.domain.shared.TimeMillis;

public class TokenFixture {

    private static final String RAW_TOKEN = "raw-token";

    private TokenFixture() {
    }

    public static Token.Builder builder() {
        return Token.builder()
                .user(UserFixture.builder().build())
                .rawToken(RAW_TOKEN)
                .expiration(0L)
                .clock(TimeMillis::now);
    }
}
