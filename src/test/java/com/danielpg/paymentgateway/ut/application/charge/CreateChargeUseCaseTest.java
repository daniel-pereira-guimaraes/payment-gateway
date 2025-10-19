package com.danielpg.paymentgateway.ut.application.charge;

import static com.danielpg.paymentgateway.fixture.AppTransactionFixture.assertThatInTransaction;
import static com.danielpg.paymentgateway.fixture.ChargeFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.danielpg.paymentgateway.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.danielpg.paymentgateway.application.charge.CreateChargeUseCase;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.CreateChargeService;
import com.danielpg.paymentgateway.fixture.AppTransactionFixture;
import com.danielpg.paymentgateway.fixture.ChargeFixture;

class CreateChargeUseCaseTest {

    private RequesterProvider requesterProvider;
    private CreateChargeService createChargeService;
    private CreateChargeUseCase useCase;
    private CreateChargeUseCase.Request createChargeRequest;
    private Charge expectedCharge;

    @BeforeEach
    void beforeEach() {
        var transaction = AppTransactionFixture.mockedTransaction();
        requesterProvider = mock(RequesterProvider.class);
        createChargeService = mock(CreateChargeService.class);
        useCase = new CreateChargeUseCase(transaction, requesterProvider, createChargeService);

        var requester = UserFixture.builder().withCpf(ISSUER_CPF).build();
        when(requesterProvider.requester()).thenReturn(requester);

        expectedCharge = ChargeFixture.builder().withId(null).build();
        createChargeRequest = new CreateChargeUseCase.Request(PAYER_CPF, AMOUNT, DESCRIPTION);

        assertThatInTransaction(transaction).when(createChargeService).createCharge(any());
        doReturn(expectedCharge).when(createChargeService).createCharge(any());
    }

    @Test
    void createsChargeSuccessfully() {
        var createdCharge = useCase.createCharge(createChargeRequest);

        assertThat(createdCharge, is(expectedCharge));
        verify(createChargeService).createCharge(any());
    }

    @Test
    void propagatesExceptionWhenServiceFails() {
        when(createChargeService.createCharge(any())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.createCharge(createChargeRequest));

        verify(createChargeService).createCharge(any());
    }

    @Test
    void propagatesExceptionWhenRequesterProviderFails() {
        when(requesterProvider.requester()).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.createCharge(createChargeRequest));

        verify(createChargeService, never()).createCharge(any());
    }
}
