package com.danielpg.paymentgateway.ut.infrastructure.shared;

import com.danielpg.paymentgateway.infrastructure.shared.SpringAppTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class SpringAppTransactionTest {

    private SpringAppTransaction appTransaction;

    @BeforeEach
    void setUp() {
        var transactionManager = mock(PlatformTransactionManager.class);
        appTransaction = new SpringAppTransaction(transactionManager);
    }

    @Test
    void returnsFalseWhenNotInTransaction() {
        assertThat(appTransaction.inTransaction(), is(false));
    }

    @Test
    void returnsTrueWhenInsideTransaction() {
        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);
        try {
            assertThat(appTransaction.inTransaction(), is(true));
        } finally {
            TransactionSynchronizationManager.setActualTransactionActive(false);
            TransactionSynchronizationManager.clearSynchronization();
        }
    }


    @Test
    void executesRunnableWhenExecuteIsCalled() {
        var runnable = mock(Runnable.class);

        appTransaction.execute(runnable);

        verify(runnable, times(1)).run();
    }

    @Test
    void doesNotThrowExceptionWhenRunnableRunsSuccessfully() {
        assertDoesNotThrow(() -> appTransaction.execute(() -> { /* no-op */ }));
    }
}
