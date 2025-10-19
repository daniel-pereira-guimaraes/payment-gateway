package com.danielpg.paymentgateway.ut.application.charge;

import com.danielpg.paymentgateway.application.charge.FindIssuedChargesUseCase;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesFilter;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesItem;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesQuery;
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

public class FindIssuedChargesUseCaseTest {

    private static final long NOW = System.currentTimeMillis();

    private static final IssuedChargesFilter FILTER =
            new IssuedChargesFilter(UserId.of(1L), Set.of());

    private static final List<IssuedChargesItem> ITEMS = List.of(
            createItem(1L, ChargeStatus.PENDING),
            createItem(2L, ChargeStatus.PAID),
            createItem(3L, ChargeStatus.PENDING),
            createItem(4L, ChargeStatus.CANCELED)
    );

    private static final FindIssuedChargesUseCase.Response EXPECTED =
            new FindIssuedChargesUseCase.Response(
                    List.of(
                            createItem(1L, ChargeStatus.PENDING),
                            createItem(3L, ChargeStatus.PENDING)
                    ),
                    List.of(createItem(2L, ChargeStatus.PAID)),
                    List.of(createItem(4L, ChargeStatus.CANCELED))
            );


    private RequesterProvider requesterProvider;
    private IssuedChargesQuery issuedChargesQuery;
    private FindIssuedChargesUseCase useCase;

    @BeforeEach
    void setUp() {
        requesterProvider = mock(RequesterProvider.class);
        issuedChargesQuery = mock(IssuedChargesQuery.class);
        useCase = new FindIssuedChargesUseCase(requesterProvider, issuedChargesQuery);
    }

    @Test
    void findSuccessfully() {
        when(requesterProvider.requesterId()).thenReturn(FILTER.issuerId());
        when(issuedChargesQuery.execute(FILTER)).thenReturn(ITEMS);

        var response = useCase.find(FILTER.statuses());

        assertThat(response, is(EXPECTED));
        verify(requesterProvider).requesterId();
        verify(issuedChargesQuery).execute(FILTER);
    }

    @Test
    void throwsExceptionWhenRequesterProviderFails() {
        when(requesterProvider.requesterId()).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.find(Set.of()));

        verify(requesterProvider).requesterId();
        verifyNoInteractions(issuedChargesQuery);
    }

    @Test
    void throwsExceptionWhenIssuedChargesQueryFails() {
        when(requesterProvider.requesterId()).thenReturn(FILTER.issuerId());
        when(issuedChargesQuery.execute(FILTER)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.find(Set.of()));

        verify(requesterProvider).requesterId();
        verify(issuedChargesQuery).execute(FILTER);
    }

    private static IssuedChargesItem createItem(Long id, ChargeStatus status) {
        return new IssuedChargesItem(
                id,
                new IssuedChargesItem.Payer("123-" + id, "Maria" + id),
                BigDecimal.valueOf(100 + id),
                "Cobrança" + id,
                NOW + id,
                NOW + id * 2,
                status,
                status == ChargeStatus.PAID ? NOW + 3 * id : null
        );
    }

}
