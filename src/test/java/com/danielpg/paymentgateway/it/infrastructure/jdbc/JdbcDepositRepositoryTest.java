package com.danielpg.paymentgateway.it.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.deposit.DepositId;
import com.danielpg.paymentgateway.domain.deposit.DepositRepository;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.it.infrastructure.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static com.danielpg.paymentgateway.fixture.DepositFixture.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class JdbcDepositRepositoryTest extends IntegrationTestBase {

    @Autowired
    private DepositRepository repository;

    @Test
    void getReturnsDepositWhenFound() {
        var deposit = builder().withId(null).build();
        repository.save(deposit);

        var result = repository.get(deposit.id()).orElseThrow();

        assertThat(result, is(deposit));
    }

    @Test
    void getReturnsEmptyWhenNotFound() {
        var id = DepositId.of(999L);

        var result = repository.get(id);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void saveInsertsNewDepositWhenIdIsNull() {
        var deposit = builder().withId(null).build();

        repository.save(deposit);

        var retrieved = repository.get(deposit.id()).orElseThrow();
        assertThat(deposit.id(), notNullValue());
        assertThat(retrieved, is(deposit));
    }

    @Test
    void saveUpdatesExistingDepositWhenIdIsNotNull() {
        var deposit = builder().withId(null).build();
        repository.save(deposit);

        var updated = builder()
                .withId(deposit.id())
                .withAmount(PositiveMoney.of(new BigDecimal("998.01")))
                .build();

        repository.save(updated);

        var reloaded = repository.get(deposit.id()).orElseThrow();
        assertThat(reloaded.amount().value(), is(updated.amount().value()));
    }
}
