package com.danielpg.paymentgateway.domain.charge.query.issued;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.shared.Validation;
import com.danielpg.paymentgateway.domain.user.UserId;

import java.util.Set;

public record IssuedChargesFilter(
        UserId issuerId,
        Set<ChargeStatus> statuses) {

    public IssuedChargesFilter {
        Validation.required(issuerId, "O ID do emitente é requerido.");
    }
}
