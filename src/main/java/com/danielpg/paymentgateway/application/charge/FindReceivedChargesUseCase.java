package com.danielpg.paymentgateway.application.charge;

import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesFilter;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesItem;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesQuery;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FindReceivedChargesUseCase {

    private final RequesterProvider requesterProvider;
    private final ReceivedChargesQuery receivedChargesQuery;

    public FindReceivedChargesUseCase(RequesterProvider requesterProvider,
                                      ReceivedChargesQuery receivedChargesQuery) {
        this.requesterProvider = requesterProvider;
        this.receivedChargesQuery = receivedChargesQuery;
    }

    public Response find(Set<ChargeStatus> statuses) {
        var filter = new ReceivedChargesFilter(requesterProvider.requesterId(), statuses);
        var items = receivedChargesQuery.execute(filter);
        return buildResponseByStatus(items);
    }

    public Response buildResponseByStatus(List<ReceivedChargesItem> receivedCharges) {
        if (receivedCharges == null || receivedCharges.isEmpty()) {
            return new Response(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        }

        var grouped = receivedCharges.stream()
                .collect(Collectors.groupingBy(ReceivedChargesItem::status));

        return new Response(
                grouped.getOrDefault(ChargeStatus.PENDING, Collections.emptyList()),
                grouped.getOrDefault(ChargeStatus.PAID, Collections.emptyList()),
                grouped.getOrDefault(ChargeStatus.CANCELED, Collections.emptyList())
        );
    }

    public record Response(
            List<ReceivedChargesItem> pendings,
            List<ReceivedChargesItem> paids,
            List<ReceivedChargesItem> canceleds) {
    }
}
