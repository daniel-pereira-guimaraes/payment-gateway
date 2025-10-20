package com.danielpg.paymentgateway.domain.charge.query.received;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.shared.Validation;
import com.danielpg.paymentgateway.domain.user.UserId;

import java.util.Set;

public record ReceivedChargesFilter(
        UserId payerId,
        Set<ChargeStatus> statuses) {

    public ReceivedChargesFilter {
        Validation.required(payerId, "O ID do pagamento é requerido.");
    }
}
