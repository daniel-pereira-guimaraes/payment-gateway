package com.danielpg.paymentgateway.ut.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.payment.PaymentAuthorizer;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentNotAuthorizedException;
import com.danielpg.paymentgateway.domain.shared.AppClock;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeNotFoundException;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentRepository;
import com.danielpg.paymentgateway.domain.charge.payment.RegisterPaymentService;
import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.user.*;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import com.danielpg.paymentgateway.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.danielpg.paymentgateway.domain.charge.ChargeStatus.PAID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterPaymentServiceTest {

    private ChargeRepository chargeRepository;
    private UserRepository userRepository;
    private PaymentRepository paymentRepository;
    private PaymentAuthorizer paymentAuthorizer;
    private RegisterPaymentService service;

    private static final TimeMillis NOW = TimeMillis.now();

    private static final Charge CHARGE = ChargeFixture.builder()
            .withAmount(PositiveMoney.of(BigDecimal.ONE))
            .build();

    private static final User ISSUER = UserFixture.builder()
            .withId(CHARGE.issuerId())
            .withBalance(Balance.of(BigDecimal.TEN))
            .build();

    private static final User PAYER = UserFixture.builder()
            .withId(CHARGE.payerId())
            .withBalance(Balance.of(BigDecimal.TEN))
            .build();

    @BeforeEach
    void setup() {
        var clock = mock(AppClock.class);
        chargeRepository = mock(ChargeRepository.class);
        userRepository = mock(UserRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        paymentAuthorizer = mock(PaymentAuthorizer.class);
        service = new RegisterPaymentService(chargeRepository,
                userRepository, paymentRepository, paymentAuthorizer, clock);

        when(paymentRepository.exists(CHARGE.id())).thenReturn(false);
        when(chargeRepository.getOrThrow(CHARGE.id())).thenReturn(CHARGE);
        when(userRepository.getOrThrow(ISSUER.id())).thenReturn(ISSUER);
        when(userRepository.getOrThrow(PAYER.id())).thenReturn(PAYER);
        when(clock.now()).thenReturn(NOW);
    }

    @Test
    void registerPaymentWhenChargeHasNoExistingPayment() {
        var payment = service.registerPayment(CHARGE.id());

        assertThat(payment, notNullValue());
        assertThat(payment.chargeId(), is(CHARGE.id()));
        assertThat(payment.paidAt(), is(NOW));
        assertThat(CHARGE.status(), is(PAID));
        assertThat(ISSUER.balance().value(), is(new BigDecimal("11.00")));
        assertThat(PAYER.balance().value(), is(new BigDecimal("9.00")));
        verify(paymentAuthorizer).authorizePayment(CHARGE);
        verify(paymentRepository).save(payment);
        verify(chargeRepository).save(CHARGE);
        verify(userRepository).save(ISSUER);
        verify(userRepository).save(PAYER);
    }

    @Test
    void throwsExceptionWhenPaymentAlreadyExists() {
        when(paymentRepository.exists(CHARGE.id())).thenReturn(true);

        var exception = assertThrows(IllegalStateException.class,
                () -> service.registerPayment(CHARGE.id())
        );

        assertThat(exception.getMessage(), is("Já existe um pagamento para esta cobrança: " + CHARGE.id().value()));
        verifyNoInteractions(chargeRepository);
        verify(paymentRepository, never()).save(any());
        verify(chargeRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void throwsExceptionWhenChargeNotFound() {
        when(chargeRepository.getOrThrow(CHARGE.id())).thenThrow(ChargeNotFoundException.class);

        assertThrows(ChargeNotFoundException.class,
                () -> service.registerPayment(CHARGE.id())
        );

        verify(paymentRepository, never()).save(any());
        verify(chargeRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void throwExceptionWhenIssuerNotFound() {
        when(userRepository.getOrThrow(ISSUER.id())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> service.registerPayment(CHARGE.id()));

        verify(paymentRepository, never()).save(any());
        verify(chargeRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void throwExceptionWhenPayerNotFound() {
        when(userRepository.getOrThrow(PAYER.id())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> service.registerPayment(CHARGE.id()));

        verify(paymentRepository, never()).save(any());
        verify(chargeRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void throwsExceptionWhenPayerHasInsufficientBalance() {
        var lowBalancePayer = UserFixture.builder()
                .withId(CHARGE.payerId())
                .withBalance(Balance.of(BigDecimal.ZERO))
                .build();

        when(userRepository.getOrThrow(PAYER.id())).thenReturn(lowBalancePayer);

        assertThrows(InsufficientBalanceException.class, () -> service.registerPayment(CHARGE.id()));

        verify(paymentRepository, never()).save(any());
        verify(chargeRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void propagatesPaymentNotAuthorizedException() {
        doThrow(new PaymentNotAuthorizedException()).when(paymentAuthorizer).authorizePayment(CHARGE);

        assertThrows(PaymentNotAuthorizedException.class,
                () -> service.registerPayment(CHARGE.id())
        );

        verify(paymentRepository, never()).save(any());
        verify(chargeRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }
}