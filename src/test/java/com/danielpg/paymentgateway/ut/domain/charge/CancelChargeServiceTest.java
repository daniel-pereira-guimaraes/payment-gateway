package com.danielpg.paymentgateway.ut.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.charge.payment.*;
import com.danielpg.paymentgateway.domain.user.Balance;
import com.danielpg.paymentgateway.domain.user.User;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import com.danielpg.paymentgateway.fixture.CreditCardFixture;
import com.danielpg.paymentgateway.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.danielpg.paymentgateway.fixture.ChargeFixture.ISSUER_ID;
import static com.danielpg.paymentgateway.fixture.ChargeFixture.PAYER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CancelPaymentServiceTest {

    private ChargeRepository chargeRepository;
    private UserRepository userRepository;
    private PaymentRepository paymentRepository;
    private PaymentAuthorizer paymentAuthorizer;
    private CancelPaymentService service;

    private Charge pendingCharge;
    private Charge paidChargeBalance;
    private Charge paidChargeCard;
    private Payment paymentBalance;
    private Payment paymentCard;

    private static final User ISSUER = UserFixture.builder()
            .withId(ISSUER_ID)
            .withBalance(Balance.of(new BigDecimal("10.00")))
            .build();

    private static final User PAYER = UserFixture.builder()
            .withId(PAYER_ID)
            .withBalance(Balance.of(new BigDecimal("10.00")))
            .build();

    @BeforeEach
    void setup() {
        chargeRepository = mock(ChargeRepository.class);
        userRepository = mock(UserRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        paymentAuthorizer = mock(PaymentAuthorizer.class);

        service = new CancelPaymentService(chargeRepository, userRepository, paymentRepository, paymentAuthorizer);

        pendingCharge = ChargeFixture.builder().withStatus(ChargeStatus.PENDING).build();
        paidChargeBalance = ChargeFixture.builder().withStatus(ChargeStatus.PAID).build();
        paidChargeCard = ChargeFixture.builder().withStatus(ChargeStatus.PAID).build();

        paymentBalance = Payment.builder()
                .withId(PaymentId.of(1L))
                .withChargeId(paidChargeBalance.id())
                .withMethod(PaymentMethod.BALANCE)
                .build();

        paymentCard = Payment.builder()
                .withId(PaymentId.of(2L))
                .withChargeId(paidChargeCard.id())
                .withMethod(PaymentMethod.CREDIT_CARD)
                .withCreditCard(CreditCardFixture.builder().build())
                .build();

        when(userRepository.getOrThrow(ISSUER.id())).thenReturn(ISSUER);
        when(userRepository.getOrThrow(PAYER.id())).thenReturn(PAYER);
    }

    @Test
    void cancelsPendingCharge() {
        when(paymentRepository.getOrThrow(any())).thenReturn(Payment.builder().withChargeId(pendingCharge.id()).build());
        when(chargeRepository.getOrThrow(any())).thenReturn(pendingCharge);

        service.cancelPayment(PaymentId.of(100L));

        assertThat(pendingCharge.status(), is(ChargeStatus.CANCELED));
        verify(chargeRepository).save(pendingCharge);
        verifyNoInteractions(userRepository);
        verify(paymentAuthorizer, never()).authorizeCancellation(any(), any());
    }

    @Test
    void cancelsPaidChargeWithBalanceAndUpdatesUsers() {
        when(paymentRepository.getOrThrow(paymentBalance.id())).thenReturn(paymentBalance);
        when(chargeRepository.getOrThrow(paymentBalance.chargeId())).thenReturn(paidChargeBalance);

        service.cancelPayment(paymentBalance.id());

        assertThat(paidChargeBalance.status(), is(ChargeStatus.CANCELED));
        assertThat(PAYER.balance().value(), is(new BigDecimal("11.00")));
        assertThat(ISSUER.balance().value(), is(new BigDecimal("9.00")));
        verify(chargeRepository).save(paidChargeBalance);
        verify(userRepository).save(ISSUER);
        verify(userRepository).save(PAYER);
        verifyNoInteractions(paymentAuthorizer);
    }

    @Test
    void cancelsPaidChargeWithCreditCardAndCallsAuthorizer() {
        when(paymentRepository.getOrThrow(paymentCard.id())).thenReturn(paymentCard);
        when(chargeRepository.getOrThrow(paymentCard.chargeId())).thenReturn(paidChargeCard);

        service.cancelPayment(paymentCard.id());

        assertThat(paidChargeCard.status(), is(ChargeStatus.CANCELED));
        verify(paymentAuthorizer).authorizeCancellation(paidChargeCard, paymentCard.creditCard());
        verify(chargeRepository).save(paidChargeCard);
        verifyNoInteractions(userRepository);
    }

    @Test
    void throwsExceptionWhenChargeStatusCannotBeCanceled() {
        var canceledCharge = ChargeFixture.builder().withStatus(ChargeStatus.CANCELED).build();
        var payment = Payment.builder().withChargeId(canceledCharge.id()).build();

        when(paymentRepository.getOrThrow(payment.id())).thenReturn(payment);
        when(chargeRepository.getOrThrow(payment.chargeId())).thenReturn(canceledCharge);

        var ex = assertThrows(IllegalStateException.class,
                () -> service.cancelPayment(payment.id())
        );

        assertThat(ex.getMessage(), is("A cobrança não pode ser cancelada no status atual: " + canceledCharge.status()));
        verifyNoInteractions(paymentAuthorizer);
        verifyNoInteractions(userRepository);
        verify(chargeRepository, never()).save(any());
    }
}
