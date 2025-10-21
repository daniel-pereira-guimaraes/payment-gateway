package com.danielpg.paymentgateway.it.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.charge.*;
import com.danielpg.paymentgateway.it.infrastructure.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.danielpg.paymentgateway.fixture.ChargeFixture.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class JdbcChargeRepositoryTest extends IntegrationTestBase {

    @Autowired
    private ChargeRepository repository;

    @Test
    void getByIdReturnsChargeWhenFound() {
        var charge = builder().withId(null).build();

        repository.save(charge);

        var result = repository.get(charge.id()).orElseThrow();
        assertThat(result, is(charge));
    }

    @Test
    void getByIdReturnsEmptyWhenNotFound() {
        var id = ChargeId.of(999L);

        var charge = repository.get(id);

        assertThat(charge.isEmpty(), is(true));
    }

    @Test
    void getOrThrowByIdThrowsExceptionWhenNotFound() {
        var id = ChargeId.of(999L);

        var exception = assertThrows(ChargeNotFoundException.class,
                () -> repository.getOrThrow(id)
        );

        assertThat(exception.getMessage(), is("Cobrança não encontrada: 999"));
    }

    @Test
    void mustAddAndGetById() {
        var charge = builder().withId(null).build();

        repository.save(charge);

        var retrieved = repository.get(charge.id()).orElseThrow();
        assertThat(charge.id(), notNullValue());
        assertThat(charge, is(retrieved));
    }

    @Test
    void mustUpdateExistingCharge() {
        var charge = builder().withId(null).build();
        repository.save(charge);

        var updatedDescription = ChargeDescription.of("Updated description");
        var updated = builder()
                .withId(charge.id())
                .withDescription(updatedDescription)
                .build();

        repository.save(updated);

        var reloaded = repository.get(charge.id()).orElseThrow();
        assertThat(reloaded.description(), is(updatedDescription));
    }

    @Test
    void mustChangeStatusToPaid() {
        var charge = builder()
                .withId(null)
                .withStatus(ChargeStatus.PENDING)
                .build();
        repository.save(charge);

        charge.changeStatusToPaid();
        repository.save(charge);

        var reloaded = repository.get(charge.id()).orElseThrow();
        assertThat(reloaded.status(), is(ChargeStatus.PAID));
    }

    @Test
    void mustChangeStatusToCanceled() {
        var charge = builder()
                .withId(null)
                .withStatus(ChargeStatus.PENDING)
                .build();
        repository.save(charge);

        charge.changeStatusToCanceled();
        repository.save(charge);

        var reloaded = repository.get(charge.id()).orElseThrow();
        assertThat(reloaded.status(), is(ChargeStatus.CANCELED));
    }
}
