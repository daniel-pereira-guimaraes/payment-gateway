package com.danielpg.paymentgateway.ut.domain.charge;

import com.danielpg.paymentgateway.domain.charge.CancelChargeService;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.charge.payment.*;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.user.Balance;
import com.danielpg.paymentgateway.domain.user.User;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import com.danielpg.paymentgateway.fixture.CreditCardFixture;
import com.danielpg.paymentgateway.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static com.danielpg.paymentgateway.fixture.ChargeFixture.ISSUER_ID;
import static com.danielpg.paymentgateway.fixture.ChargeFixture.PAYER_ID;
import static com.danielpg.paymentgateway.fixture.PaymentFixture.PAID_AT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CancelChargeServiceTest {

    private ChargeRepository chargeRepository;
    private UserRepository userRepository;
    private PaymentRepository paymentRepository;
    private PaymentAuthorizer paymentAuthorizer;
    private CancelChargeService service;

    private Charge pendingCharge;
    private Charge paidChargeBalance;
    private Charge paidChargeCard;
    private Payment paymentBalance;
    private Payment paymentCard;
    private User issuer;
    private User payer;

    @BeforeEach
    void setup() {
        chargeRepository = mock(ChargeRepository.class);
        userRepository = mock(UserRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        paymentAuthorizer = mock(PaymentAuthorizer.class);

        service = new CancelChargeService(chargeRepository, userRepository, paymentRepository, paymentAuthorizer);

        pendingCharge = ChargeFixture.builder()
                .withStatus(ChargeStatus.PENDING)
                .build();

        paidChargeBalance = ChargeFixture.builder()
                .withStatus(ChargeStatus.PAID)
                .withAmount(PositiveMoney.of(BigDecimal.TWO))
                .build();

        paidChargeCard = ChargeFixture.builder()
                .withStatus(ChargeStatus.PAID)
                .build();

        paymentBalance = Payment.builder()
                .withId(PaymentId.of(1L))
                .withChargeId(paidChargeBalance.id())
                .withMethod(PaymentMethod.BALANCE)
                .withPaidAt(PAID_AT)
                .build();

        paymentCard = Payment.builder()
                .withId(PaymentId.of(2L))
                .withChargeId(paidChargeCard.id())
                .withMethod(PaymentMethod.CREDIT_CARD)
                .withCreditCard(CreditCardFixture.builder().build())
                .withPaidAt(PAID_AT)
                .build();

        issuer = UserFixture.builder()
                .withId(ISSUER_ID)
                .withBalance(Balance.of(new BigDecimal("20.00")))
                .build();

        payer = UserFixture.builder()
                .withId(PAYER_ID)
                .withBalance(Balance.of(new BigDecimal("10.00")))
                .build();

        when(userRepository.getOrThrow(issuer.id())).thenReturn(issuer);
        when(userRepository.getOrThrow(payer.id())).thenReturn(payer);
    }

    @Test
    void cancelsPendingCharge() {
        service.cancelCharge(pendingCharge);

        assertThat(pendingCharge.status(), is(ChargeStatus.CANCELED));
        verify(chargeRepository).save(pendingCharge);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(paymentAuthorizer);
    }

    @Test
    void cancelsPaidChargeWithBalanceAndUpdatesUsers() {
        when(paymentRepository.get(paidChargeBalance.id()))
                .thenReturn(Optional.of(paymentBalance));

        service.cancelCharge(paidChargeBalance);

        assertThat(paidChargeBalance.status(), is(ChargeStatus.CANCELED));
        assertThat(issuer.balance().value(), is(new BigDecimal("18.00")));
        assertThat(payer.balance().value(), is(new BigDecimal("12.00")));
        verify(chargeRepository).save(paidChargeBalance);
        verify(userRepository).save(issuer);
        verify(userRepository).save(payer);
        verifyNoInteractions(paymentAuthorizer);
    }

    @Test
    void cancelsPaidChargeWithCreditCardAndCallsAuthorizer() {
        when(paymentRepository.get(paidChargeCard.id()))
                .thenReturn(Optional.of(paymentCard));

        service.cancelCharge(paidChargeCard);

        assertThat(paidChargeCard.status(), is(ChargeStatus.CANCELED));
        verify(paymentAuthorizer).authorizeCancellation(paidChargeCard, paymentCard.creditCard());
        verify(chargeRepository).save(paidChargeCard);
        verifyNoInteractions(userRepository);
    }

    @Test
    void throwsExceptionWhenChargeStatusCannotBeCanceled() {
        var canceledCharge = ChargeFixture.builder().withStatus(ChargeStatus.CANCELED).build();

        var ex = assertThrows(IllegalStateException.class,
                () -> service.cancelCharge(canceledCharge)
        );

        assertThat(ex.getMessage(), is("A cobrança não pode ser cancelada no status atual: " + canceledCharge.status()));
        verifyNoInteractions(paymentAuthorizer);
        verifyNoInteractions(userRepository);
        verify(chargeRepository, never()).save(any());
    }
}
