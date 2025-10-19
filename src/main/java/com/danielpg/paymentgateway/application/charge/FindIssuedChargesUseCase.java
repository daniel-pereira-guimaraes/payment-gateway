package com.danielpg.paymentgateway.application.charge;

import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesFilter;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesItem;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesQuery;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FindIssuedChargesUseCase {

    private final RequesterProvider requesterProvider;
    private final IssuedChargesQuery issuedChargesQuery;

    public FindIssuedChargesUseCase(RequesterProvider requesterProvider,
                                    IssuedChargesQuery issuedChargesQuery) {
        this.requesterProvider = requesterProvider;
        this.issuedChargesQuery = issuedChargesQuery;
    }

    public Response find(Set<ChargeStatus> statuses) {
        var filter = new IssuedChargesFilter(requesterProvider.requesterId(), statuses);
        var items = issuedChargesQuery.execute(filter);
        return buildResponseByStatus(items);
    }

    public Response buildResponseByStatus(List<IssuedChargesItem> issuedCharges) {

        if (issuedCharges == null || issuedCharges.isEmpty()) {
            return new Response(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        }

        var grouped = issuedCharges.stream()
                .collect(Collectors.groupingBy(IssuedChargesItem::status));

        return new Response(
                grouped.getOrDefault(ChargeStatus.PENDING, Collections.emptyList()),
                grouped.getOrDefault(ChargeStatus.PAID, Collections.emptyList()),
                grouped.getOrDefault(ChargeStatus.CANCELED, Collections.emptyList())
        );
    }

    public record Response(
            List<IssuedChargesItem> pendings,
            List<IssuedChargesItem> paids,
            List<IssuedChargesItem> canceleds) {
    }
}