package com.danielpg.paymentgateway.domain.charge.query.issued;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;

import java.math.BigDecimal;

public record IssuedChargesItem(
        Long chargeId,
        Payer payer,
        BigDecimal amount,
        String description,
        Long createdAt,
        Long dueAt,
        ChargeStatus status,
        Long paidAt) {

    public record Payer(
            String cpf,
            String name) {
    }

}
