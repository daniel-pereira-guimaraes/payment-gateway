package com.danielpg.paymentgateway.it.infrastructure.shared;

import com.danielpg.paymentgateway.application.shared.AppTransaction;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import com.danielpg.paymentgateway.it.infrastructure.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.danielpg.paymentgateway.fixture.UserFixture.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SpringAppTransactionTest extends IntegrationTestBase {

    @Autowired
    private AppTransaction appTransaction;

    @Autowired
    private UserRepository repository;

    @Test
    void executeAddsUserWhenNoError() {
        var user = builder().withId(null).build();

        appTransaction.execute(() -> repository.save(user));

        assertThat(user.id(), notNullValue());
        assertThat(repository.get(user.id()).orElseThrow(), is(user));
    }

    @Test
    void executeDoesNotAddUserWhenExceptionOccurs() {
        var user = builder().withId(null).build();

        assertThrows(RuntimeException.class, () -> appTransaction.execute(() -> {
            repository.save(user);
            throw new RuntimeException("Falha após INSERT e antes do COMMIT");
        }));

        assertThat(repository.get(user.id()).isEmpty(), is(true));
    }
}