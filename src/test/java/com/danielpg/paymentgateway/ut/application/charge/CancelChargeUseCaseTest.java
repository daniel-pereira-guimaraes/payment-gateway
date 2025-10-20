package com.danielpg.paymentgateway.ut.application.charge;

import static com.danielpg.paymentgateway.fixture.AppTransactionFixture.assertThatInTransaction;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.danielpg.paymentgateway.application.auth.AccessForbiddenException;
import com.danielpg.paymentgateway.application.charge.CancelChargeUseCase;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.CancelChargeService;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.fixture.AppTransactionFixture;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CancelChargeUseCaseTest {

    private RequesterProvider requesterProvider;
    private ChargeRepository chargeRepository;
    private CancelChargeService cancelChargeService;
    private CancelChargeUseCase useCase;
    private Charge charge;

    @BeforeEach
    void beforeEach() {
        var transaction = AppTransactionFixture.mockedTransaction();

        requesterProvider = mock(RequesterProvider.class);
        chargeRepository = mock(ChargeRepository.class);
        cancelChargeService = mock(CancelChargeService.class);

        useCase = new CancelChargeUseCase(transaction, chargeRepository, requesterProvider, cancelChargeService);

        charge = ChargeFixture.builder().build();
        when(chargeRepository.getOrThrow(charge.id())).thenReturn(charge);
        when(requesterProvider.requesterId()).thenReturn(charge.issuerId());

        assertThatInTransaction(transaction).when(cancelChargeService).cancelCharge(charge);
    }

    @Test
    void cancelsChargeSuccessfully() {
        useCase.cancelCharge(charge.id());

        verify(cancelChargeService).cancelCharge(charge);
    }

    @Test
    void throwsAccessForbiddenWhenRequesterIsNotIssuer() {
        var otherUserId = charge.payerId();
        when(requesterProvider.requesterId()).thenReturn(otherUserId);

        assertThrows(AccessForbiddenException.class, () -> useCase.cancelCharge(charge.id()));

        verify(cancelChargeService, never()).cancelCharge(any());
    }

    @Test
    void propagatesExceptionWhenServiceFails() {
        doThrow(RuntimeException.class).when(cancelChargeService).cancelCharge(charge);

        assertThrows(RuntimeException.class, () -> useCase.cancelCharge(charge.id()));

        verify(cancelChargeService).cancelCharge(charge);
    }

    @Test
    void propagatesExceptionWhenChargeRepositoryFails() {
        doThrow(RuntimeException.class).when(chargeRepository).getOrThrow(charge.id());

        assertThrows(RuntimeException.class, () -> useCase.cancelCharge(charge.id()));

        verify(cancelChargeService, never()).cancelCharge(any());
    }
}
