package com.danielpg.paymentgateway.ut.domain.charge.payment;

import com.danielpg.paymentgateway.domain.AppClock;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentRepository;
import com.danielpg.paymentgateway.domain.charge.payment.RegisterPaymentService;
import com.danielpg.paymentgateway.domain.TimeMillis;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterPaymentServiceTest {

    private ChargeRepository chargeRepository;
    private PaymentRepository paymentRepository;
    private AppClock clock;
    private RegisterPaymentService service;

    private final Charge charge = ChargeFixture.builder().build();

    @BeforeEach
    void setup() {
        chargeRepository = mock(ChargeRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        clock = mock(AppClock.class);
        service = new RegisterPaymentService(chargeRepository, paymentRepository, clock);
    }

    @Test
    void registerPaymentWhenChargeHasNoExistingPayment() {
        when(paymentRepository.exists(charge.id())).thenReturn(false);
        when(chargeRepository.getOrThrow(charge.id())).thenReturn(charge);
        when(clock.now()).thenReturn(TimeMillis.of(1500L));

        var payment = service.registerPayment(charge.id());

        assertThat(payment, notNullValue());
        assertThat(payment.chargeId(), is(charge.id()));
        assertThat(payment.paidAt().value(), is(1500L));
        verify(paymentRepository).save(payment);
    }

    @Test
    void throwsExceptionWhenPaymentAlreadyExists() {
        when(paymentRepository.exists(charge.id())).thenReturn(true);

        var exception = assertThrows(IllegalStateException.class,
                () -> service.registerPayment(charge.id())
        );

        assertThat(exception.getMessage(), is("Já existe um pagamento para esta cobrança: " + charge.id().value()));
        verifyNoInteractions(chargeRepository);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void registerPaymentWithCurrentTimestampFromClock() {
        when(paymentRepository.exists(charge.id())).thenReturn(false);
        when(chargeRepository.getOrThrow(charge.id())).thenReturn(charge);
        var now = TimeMillis.of(123456789L);
        when(clock.now()).thenReturn(now);

        var payment = service.registerPayment(charge.id());

        assertThat(payment.paidAt(), is(now));
    }

}