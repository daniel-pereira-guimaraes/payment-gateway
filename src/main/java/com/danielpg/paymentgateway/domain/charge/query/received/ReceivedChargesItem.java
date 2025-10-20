package com.danielpg.paymentgateway.domain.charge.query.received;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;

import java.math.BigDecimal;

public record ReceivedChargesItem(
        Long chargeId,
        Issuer issuer,
        BigDecimal amount,
        String description,
        Long createdAt,
        Long dueAt,
        ChargeStatus status,
        Long paidAt) {

    public record Issuer(
            String cpf,
            String name) {
    }

}
