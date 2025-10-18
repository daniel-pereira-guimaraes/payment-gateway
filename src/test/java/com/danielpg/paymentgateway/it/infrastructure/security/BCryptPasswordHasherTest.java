package com.danielpg.paymentgateway.it.infrastructure.security;

import com.danielpg.paymentgateway.infrastructure.security.BCryptPasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.danielpg.paymentgateway.fixture.UserFixture.PLAIN_TEXT_PASSWORD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class BCryptPasswordHasherTest {

    private BCryptPasswordHasher hasher;

    @BeforeEach
    void beforeEach() {
        hasher = new BCryptPasswordHasher();
    }

    @Test
    void hashStartsWithVersionAndMatchesPlainText() {
        var hashedPassword = hasher.hashedPassword(PLAIN_TEXT_PASSWORD);

        assertThat(PLAIN_TEXT_PASSWORD.value().startsWith("$2a$10$"), is(false));
        assertThat(hashedPassword.hash().startsWith("$2a$10$"), is(true));
        assertThat(hasher.matches(PLAIN_TEXT_PASSWORD, hashedPassword), is(true));
    }

    @Test
    void generatesDifferentHashesForSamePlainText() {
        var hashedPassword1 = hasher.hashedPassword(PLAIN_TEXT_PASSWORD);
        var hashedPassword2 = hasher.hashedPassword(PLAIN_TEXT_PASSWORD);

        assertThat(hashedPassword1.hash(), not(hashedPassword2.hash()));
    }

}
