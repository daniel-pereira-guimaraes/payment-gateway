package com.danielpg.paymentgateway.ut.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.payment.*;
import com.danielpg.paymentgateway.domain.shared.AppClock;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.user.*;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import com.danielpg.paymentgateway.fixture.UserFixture;
import com.danielpg.paymentgateway.fixture.CreditCardFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static com.danielpg.paymentgateway.domain.charge.ChargeStatus.PAID;
import static com.danielpg.paymentgateway.domain.charge.ChargeStatus.PENDING;
import static com.danielpg.paymentgateway.fixture.ChargeFixture.ISSUER_ID;
import static com.danielpg.paymentgateway.fixture.ChargeFixture.PAYER_ID;
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
    private Charge charge;

    private static final TimeMillis NOW = TimeMillis.now();

    private static final User ISSUER = UserFixture.builder()
            .withId(ISSUER_ID)
            .withBalance(Balance.of(BigDecimal.TEN))
            .build();

    private static final User PAYER = UserFixture.builder()
            .withId(PAYER_ID)
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

        charge = ChargeFixture.builder()
                .withAmount(PositiveMoney.of(BigDecimal.ONE))
                .withStatus(PENDING)
                .build();

        when(paymentRepository.exists(charge.id())).thenReturn(false);
        when(chargeRepository.getOrThrow(charge.id())).thenReturn(charge);
        when(userRepository.getOrThrow(ISSUER.id())).thenReturn(ISSUER);
        when(userRepository.getOrThrow(PAYER.id())).thenReturn(PAYER);
        when(clock.now()).thenReturn(NOW);
    }

    @Test
    void registerPaymentWithBalance() {
        var request = RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(PaymentMethod.BALANCE)
                .build();

        var payment = service.registerPayment(request);

        assertThat(payment, notNullValue());
        assertThat(payment.chargeId(), is(charge.id()));
        assertThat(payment.method(), is(PaymentMethod.BALANCE));
        assertThat(payment.creditCard(), nullValue());
        assertThat(payment.paidAt(), is(NOW));
        assertThat(charge.status(), is(PAID));
        assertThat(ISSUER.balance().value(), is(new BigDecimal("11.00")));
        assertThat(PAYER.balance().value(), is(new BigDecimal("9.00")));

        verify(paymentAuthorizer, never()).authorizePayment(any(), any());
        verify(paymentRepository).save(payment);
        verify(chargeRepository).save(charge);
        verify(userRepository).save(ISSUER);
        verify(userRepository).save(PAYER);
    }

    @Test
    void registerPaymentWithCreditCard() {
        var creditCard = CreditCardFixture.builder().build();
        var request = RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(PaymentMethod.CREDIT_CARD)
                .withCreditCard(creditCard)
                .build();

        var payment = service.registerPayment(request);

        assertThat(payment, notNullValue());
        assertThat(payment.chargeId(), is(charge.id()));
        assertThat(payment.method(), is(PaymentMethod.CREDIT_CARD));
        assertThat(payment.creditCard(), is(creditCard));
        assertThat(payment.paidAt(), is(NOW));
        assertThat(charge.status(), is(PAID));

        verify(paymentAuthorizer).authorizePayment(charge, request.creditCard());
        verify(paymentRepository).save(payment);
        verify(chargeRepository).save(charge);
        verify(userRepository, never()).save(any());
    }

    @Test
    void throwsExceptionWhenPaymentAlreadyExists() {
        when(paymentRepository.exists(charge.id())).thenReturn(true);
        var request = RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(PaymentMethod.BALANCE)
                .build();

        var exception = assertThrows(IllegalStateException.class,
                () -> service.registerPayment(request));

        assertThat(exception.getMessage(), is("Já existe um pagamento para esta cobrança: " + charge.id().value()));
        verifyNoInteractions(chargeRepository);
        verify(paymentRepository, never()).save(any());
        verify(chargeRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void propagatesPaymentNotAuthorizedException() {
        doThrow(PaymentNotAuthorizedException.class).when(paymentAuthorizer).authorizePayment(any(), any());
        var creditCard = CreditCardFixture.builder().build();
        var request = RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(PaymentMethod.CREDIT_CARD)
                .withCreditCard(creditCard)
                .build();

        assertThrows(PaymentNotAuthorizedException.class,
                () -> service.registerPayment(request));

        verify(paymentRepository, never()).save(any());
        verify(chargeRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = ChargeStatus.class, names = "PENDING", mode = EnumSource.Mode.EXCLUDE)
    void cannotRegisterPaymentWhenChargeIsNotPending(ChargeStatus nonPendingStatus) {
        var nonPendingCharge = ChargeFixture.builder()
                .withAmount(PositiveMoney.of(BigDecimal.ONE))
                .withStatus(nonPendingStatus)
                .build();

        when(chargeRepository.getOrThrow(nonPendingCharge.id())).thenReturn(nonPendingCharge);

        var request = RegisterPaymentRequest.builder()
                .withCharge(nonPendingCharge)
                .withMethod(PaymentMethod.BALANCE)
                .build();

        var exception = assertThrows(IllegalStateException.class, () -> service.registerPayment(request));

        assertThat(exception.getMessage(), is("A cobrança não está pendente."));
        verify(paymentRepository, never()).save(any());
        verify(chargeRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }
}
