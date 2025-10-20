package com.danielpg.paymentgateway.ut.application.deposit;

import static com.danielpg.paymentgateway.fixture.AppTransactionFixture.assertThatInTransaction;
import static com.danielpg.paymentgateway.fixture.DepositFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.danielpg.paymentgateway.application.deposit.CreateDepositUseCase;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.deposit.CreateDepositService;
import com.danielpg.paymentgateway.domain.deposit.Deposit;
import com.danielpg.paymentgateway.domain.deposit.DepositRequest;
import com.danielpg.paymentgateway.fixture.AppTransactionFixture;
import com.danielpg.paymentgateway.fixture.DepositFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CreateDepositUseCaseTest {

    private RequesterProvider requesterProvider;
    private CreateDepositService createDepositService;
    private CreateDepositUseCase useCase;
    private Deposit expectedDeposit;

    @BeforeEach
    void beforeEach() {
        var transaction = AppTransactionFixture.mockedTransaction();
        requesterProvider = mock(RequesterProvider.class);
        createDepositService = mock(CreateDepositService.class);
        useCase = new CreateDepositUseCase(transaction, requesterProvider, createDepositService);

        when(requesterProvider.requesterId()).thenReturn(USER_ID);

        expectedDeposit = DepositFixture.builder().withId(null).build();

        assertThatInTransaction(transaction).when(createDepositService).createDeposit(any());
        doReturn(expectedDeposit).when(createDepositService).createDeposit(any());
    }

    @Test
    void createsDepositSuccessfully() {
        var createdDeposit = useCase.createDeposit(AMOUNT);

        assertThat(createdDeposit, is(expectedDeposit));

        var captor = ArgumentCaptor.forClass(DepositRequest.class);
        verify(createDepositService).createDeposit(captor.capture());

        var actualRequest = captor.getValue();
        assertThat(actualRequest.userId(), is(USER_ID));
        assertThat(actualRequest.amount(), is(AMOUNT));
    }

    @Test
    void propagatesExceptionWhenRequesterProviderFails() {
        when(requesterProvider.requesterId()).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.createDeposit(AMOUNT));

        verify(createDepositService, never()).createDeposit(any());
    }

    @Test
    void propagatesExceptionWhenServiceFails() {
        when(createDepositService.createDeposit(any())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.createDeposit(AMOUNT));

        verify(createDepositService).createDeposit(any());
    }

}
