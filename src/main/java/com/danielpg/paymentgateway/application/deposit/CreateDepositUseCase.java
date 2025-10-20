package com.danielpg.paymentgateway.application.deposit;

import com.danielpg.paymentgateway.application.shared.AppTransaction;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.deposit.CreateDepositService;
import com.danielpg.paymentgateway.domain.deposit.Deposit;
import com.danielpg.paymentgateway.domain.deposit.DepositRequest;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;

import java.util.concurrent.atomic.AtomicReference;

public class CreateDepositUseCase {

    private final AppTransaction appTransaction;
    private final RequesterProvider requesterProvider;
    private final CreateDepositService createDepositService;

    public CreateDepositUseCase(AppTransaction appTransaction,
                                RequesterProvider requesterProvider,
                                CreateDepositService createDepositService) {
        this.appTransaction = appTransaction;
        this.requesterProvider = requesterProvider;
        this.createDepositService = createDepositService;
    }

    public Deposit createDeposit(PositiveMoney amount) {
        var depositRef = new AtomicReference<Deposit>();
        appTransaction.execute(() -> depositRef.set(internalCreateDeposit(amount)));
        return depositRef.get();
    }

    private Deposit internalCreateDeposit(PositiveMoney amount) {
        var serviceRequest = DepositRequest.of(requesterProvider.requesterId(), amount);
        return createDepositService.createDeposit(serviceRequest);
    }
}
