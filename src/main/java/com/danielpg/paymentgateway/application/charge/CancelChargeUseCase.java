package com.danielpg.paymentgateway.application.charge;

import com.danielpg.paymentgateway.application.auth.AccessForbiddenException;
import com.danielpg.paymentgateway.application.shared.AppTransaction;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.CancelChargeService;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;

public class CancelChargeUseCase {

    private final AppTransaction appTransaction;
    private final ChargeRepository chargeRepository;
    private final RequesterProvider requesterProvider;
    private final CancelChargeService cancelChargeService;

    public CancelChargeUseCase(AppTransaction appTransaction,
                               ChargeRepository chargeRepository,
                               RequesterProvider requesterProvider,
                               CancelChargeService cancelChargeService) {
        this.appTransaction = appTransaction;
        this.chargeRepository = chargeRepository;
        this.requesterProvider = requesterProvider;
        this.cancelChargeService = cancelChargeService;
    }

    public void cancelCharge(ChargeId id) {
        appTransaction.execute(() -> {
            var charge = chargeRepository.getOrThrow(id);
            validateAccessToCharge(charge);
            cancelChargeService.cancelCharge(charge);
        });
    }

    private void validateAccessToCharge(Charge charge) {
        if (!requesterProvider.requesterId().equals(charge.issuerId())) {
            throw new AccessForbiddenException();
        }
    }
}
