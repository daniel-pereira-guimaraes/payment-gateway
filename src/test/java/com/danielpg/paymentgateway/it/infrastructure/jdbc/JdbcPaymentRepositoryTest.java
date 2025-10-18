package com.danielpg.paymentgateway.it.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.TimeMillis;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.payment.Payment;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentId;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentRepository;
import com.danielpg.paymentgateway.fixture.PaymentFixture;
import com.danielpg.paymentgateway.it.infrastructure.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.danielpg.paymentgateway.fixture.PaymentFixture.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class JdbcPaymentRepositoryTest extends IntegrationTestBase {

    @Autowired
    private PaymentRepository repository;

    @Test
    void getByIdReturnsPaymentWhenFound() {
        var payment = repository.get(PaymentId.of(1L)).orElseThrow();

        assertThat(payment.id().value(), is(1L));
        assertThat(payment.chargeId().value(), is(2L));
        assertThat(payment.paidAt().value(), is(1700007300L));
    }

    @Test
    void getByIdReturnsEmptyWhenNotFound() {
        var result = repository.get(PaymentId.of(999L));

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void existsReturnsTrueWhenPaymentExistsForCharge() {
        assertThat(repository.exists(ChargeId.of(2L)), is(true));
    }

    @Test
    void existsReturnsFalseWhenPaymentDoesNotExistForCharge() {
        assertThat(repository.exists(ChargeId.of(1L)), is(false));
    }

    @Test
    void mustAddAndGetById() {
        var newPayment = builder()
                .withId(null)
                .withChargeId(ChargeId.of(1L))
                .build();

        repository.save(newPayment);

        var retrieved = repository.get(newPayment.id()).orElseThrow();
        assertThat(retrieved, is(newPayment));
    }

    @Test
    void mustUpdatePaidAt() {
        var payment = repository.get(PaymentId.of(1L)).orElseThrow();
        var updated = Payment.builder()
                .withId(payment.id())
                .withChargeId(payment.chargeId())
                .withPaidAt(TimeMillis.now())
                .build();

        repository.save(updated);

        var reloaded = repository.get(payment.id()).orElseThrow();
        assertThat(reloaded, is(updated));
    }
}
