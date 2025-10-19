package com.danielpg.paymentgateway.ut.application.charge.payment;

import static com.danielpg.paymentgateway.fixture.AppTransactionFixture.assertThatInTransaction;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.danielpg.paymentgateway.application.auth.AccessForbiddenException;
import com.danielpg.paymentgateway.application.charge.payment.RegisterPaymentUseCase;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.charge.payment.*;
import com.danielpg.paymentgateway.domain.user.UserId;
import com.danielpg.paymentgateway.fixture.AppTransactionFixture;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import com.danielpg.paymentgateway.fixture.CreditCardFixture;
import com.danielpg.paymentgateway.fixture.PaymentFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegisterPaymentUseCaseTest {

    private RequesterProvider requesterProvider;
    private RegisterPaymentService registerPaymentService;
    private ChargeRepository chargeRepository;
    private RegisterPaymentUseCase useCase;
    private Charge charge;

    @BeforeEach
    void beforeEach() {
        var transaction = AppTransactionFixture.mockedTransaction();

        requesterProvider = mock(RequesterProvider.class);
        registerPaymentService = mock(RegisterPaymentService.class);
        chargeRepository = mock(ChargeRepository.class);

        useCase = new RegisterPaymentUseCase(transaction, chargeRepository, requesterProvider, registerPaymentService);

        charge = ChargeFixture.builder().build();

        when(chargeRepository.getOrThrow(charge.id())).thenReturn(charge);
        when(requesterProvider.requesterId()).thenReturn(charge.payerId());
        assertThatInTransaction(transaction).when(registerPaymentService).registerPayment(any());
    }

    @Test
    void registersPaymentWithBalanceSuccessfully() {
        var request = new RegisterPaymentUseCase.Request(charge.id(), PaymentMethod.BALANCE, null);
        var expectedServiceRequest = RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(PaymentMethod.BALANCE)
                .build();
        var expectedPayment = PaymentFixture.builder()
                .withChargeId(charge.id())
                .withMethod(PaymentMethod.BALANCE)
                .build();
        doReturn(expectedPayment).when(registerPaymentService).registerPayment(expectedServiceRequest);

        var payment = useCase.registerPayment(request);

        assertThat(payment, is(expectedPayment));
        verify(registerPaymentService).registerPayment(expectedServiceRequest);
    }

    @Test
    void registersPaymentWithCreditCardSuccessfully() {
        var creditCard = CreditCardFixture.builder().build();

        var expectedPayment = PaymentFixture.builder()
                .withChargeId(charge.id())
                .withMethod(PaymentMethod.CREDIT_CARD)
                .withCreditCard(creditCard)
                .build();

        var expectedServiceRequest = RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(PaymentMethod.CREDIT_CARD)
                .withCreditCard(creditCard)
                .build();

        doReturn(expectedPayment).when(registerPaymentService).registerPayment(expectedServiceRequest);

        var request = new RegisterPaymentUseCase.Request(charge.id(), PaymentMethod.CREDIT_CARD, creditCard);
        var payment = useCase.registerPayment(request);

        assertThat(payment, is(expectedPayment));
        verify(registerPaymentService).registerPayment(expectedServiceRequest);
    }

    @Test
    void throwsAccessForbiddenWhenRequesterIsNotPayer() {
        var currentUserId = UserId.of(999L);
        var request = new RegisterPaymentUseCase.Request(charge.id(), PaymentMethod.BALANCE, null);
        when(requesterProvider.requesterId()).thenReturn(currentUserId);

        assertThrows(AccessForbiddenException.class, () -> useCase.registerPayment(request));

        assertThat(currentUserId, not(charge.payerId()));
        verify(registerPaymentService, never()).registerPayment(any());
    }

    @Test
    void propagatesExceptionWhenServiceFails() {
        var request = new RegisterPaymentUseCase.Request(charge.id(), PaymentMethod.BALANCE, null);
        doThrow(RuntimeException.class).when(registerPaymentService).registerPayment(any());

        assertThrows(RuntimeException.class, () -> useCase.registerPayment(request));

        verify(registerPaymentService).registerPayment(any());
    }

    @Test
    void propagatesExceptionWhenChargeRepositoryFails() {
        doThrow(RuntimeException.class).when(chargeRepository).getOrThrow(charge.id());

        var request = new RegisterPaymentUseCase.Request(charge.id(), PaymentMethod.BALANCE, null);
        assertThrows(RuntimeException.class, () -> useCase.registerPayment(request));

        verify(registerPaymentService, never()).registerPayment(any());
    }
}
