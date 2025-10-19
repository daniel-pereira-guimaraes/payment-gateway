package com.danielpg.paymentgateway.application.charge;

import com.danielpg.paymentgateway.application.shared.AppTransaction;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.CreateChargeService;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.user.Cpf;

import java.util.concurrent.atomic.AtomicReference;

public class CreateChargeUseCase {

    private final AppTransaction appTransaction;
    private final RequesterProvider requesterProvider;
    private final CreateChargeService createChargeService;

    public CreateChargeUseCase(AppTransaction appTransaction,
                               RequesterProvider requesterProvider,
                               CreateChargeService createChargeService) {
        this.appTransaction = appTransaction;
        this.requesterProvider = requesterProvider;
        this.createChargeService = createChargeService;
    }

    public Charge createCharge(Request request) {
        var chargeReference = new AtomicReference<Charge>();
        appTransaction.execute(() -> chargeReference.set(internalCreateCharge(request)));
        return chargeReference.get();
    }

    private Charge internalCreateCharge(Request request) {
        var serviceRequest = buildServiceRequest(request);
        return createChargeService.createCharge(serviceRequest);
    }

    private CreateChargeService.Request buildServiceRequest(Request request) {
        return new CreateChargeService.Request(
                requesterProvider.requester().cpf(),
                request.payerCpf,
                request.amount,
                request.description
        );
    }

    public record Request(
            Cpf payerCpf,
            PositiveMoney amount,
            String description) {
    }
}
