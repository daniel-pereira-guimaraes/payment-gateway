package com.danielpg.paymentgateway.ut.application.charge;

import com.danielpg.paymentgateway.application.charge.FindReceivedChargesUseCase;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesFilter;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesItem;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesQuery;
import com.danielpg.paymentgateway.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class FindReceivedChargesUseCaseTest {

    private static final long NOW = System.currentTimeMillis();

    private static final ReceivedChargesFilter FILTER =
            new ReceivedChargesFilter(UserId.of(1L), Set.of());

    private static final List<ReceivedChargesItem> ITEMS = List.of(
            createItem(1L, ChargeStatus.PENDING),
            createItem(2L, ChargeStatus.PAID),
            createItem(3L, ChargeStatus.PENDING),
            createItem(4L, ChargeStatus.CANCELED)
    );

    private static final FindReceivedChargesUseCase.Response EXPECTED =
            new FindReceivedChargesUseCase.Response(
                    List.of(
                            createItem(1L, ChargeStatus.PENDING),
                            createItem(3L, ChargeStatus.PENDING)
                    ),
                    List.of(createItem(2L, ChargeStatus.PAID)),
                    List.of(createItem(4L, ChargeStatus.CANCELED))
            );

    private RequesterProvider requesterProvider;
    private ReceivedChargesQuery receivedChargesQuery;
    private FindReceivedChargesUseCase useCase;

    @BeforeEach
    void setUp() {
        requesterProvider = mock(RequesterProvider.class);
        receivedChargesQuery = mock(ReceivedChargesQuery.class);
        useCase = new FindReceivedChargesUseCase(requesterProvider, receivedChargesQuery);
    }

    @Test
    void findSuccessfully() {
        when(requesterProvider.requesterId()).thenReturn(FILTER.payerId());
        when(receivedChargesQuery.execute(FILTER)).thenReturn(ITEMS);

        var response = useCase.find(FILTER.statuses());

        assertThat(response, is(EXPECTED));
        verify(requesterProvider).requesterId();
        verify(receivedChargesQuery).execute(FILTER);
    }

    @Test
    void throwsExceptionWhenRequesterProviderFails() {
        when(requesterProvider.requesterId()).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.find(Set.of()));

        verify(requesterProvider).requesterId();
        verifyNoInteractions(receivedChargesQuery);
    }

    @Test
    void throwsExceptionWhenReceivedChargesQueryFails() {
        when(requesterProvider.requesterId()).thenReturn(FILTER.payerId());
        when(receivedChargesQuery.execute(FILTER)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.find(Set.of()));

        verify(requesterProvider).requesterId();
        verify(receivedChargesQuery).execute(FILTER);
    }

    private static ReceivedChargesItem createItem(Long id, ChargeStatus status) {
        return new ReceivedChargesItem(
                id,
                new ReceivedChargesItem.Issuer("321-" + id, "João" + id),
                BigDecimal.valueOf(200 + id),
                "Recebimento" + id,
                NOW + id,
                NOW + id * 2,
                status,
                status == ChargeStatus.PAID ? NOW + 3 * id : null
        );
    }
}
