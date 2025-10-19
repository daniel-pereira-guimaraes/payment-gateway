package com.danielpg.paymentgateway.application.charge.payment;

import com.danielpg.paymentgateway.application.auth.AccessForbiddenException;
import com.danielpg.paymentgateway.application.shared.AppTransaction;
import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentMethod;
import com.danielpg.paymentgateway.domain.charge.payment.RegisterPaymentRequest;
import com.danielpg.paymentgateway.domain.charge.payment.RegisterPaymentService;
import com.danielpg.paymentgateway.domain.charge.payment.Payment;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;

import java.util.concurrent.atomic.AtomicReference;

public class RegisterPaymentUseCase {

    private final AppTransaction appTransaction;
    private final ChargeRepository chargeRepository;
    private final RequesterProvider requesterProvider;
    private final RegisterPaymentService registerPaymentService;

    public RegisterPaymentUseCase(AppTransaction appTransaction,
                                  ChargeRepository chargeRepository,
                                  RequesterProvider requesterProvider,
                                  RegisterPaymentService registerPaymentService) {
        this.appTransaction = appTransaction;
        this.chargeRepository = chargeRepository;
        this.requesterProvider = requesterProvider;
        this.registerPaymentService = registerPaymentService;
    }

    public Payment registerPayment(Request request) {
        var paymentReference = new AtomicReference<Payment>();
        appTransaction.execute(() -> paymentReference.set(internalRegisterPayment(request)));
        return paymentReference.get();
    }

    private Payment internalRegisterPayment(Request request) {
        var charge = chargeRepository.getOrThrow(request.chargeId);
        validateAccessToCharge(charge);
        var serviceRequest = buildServiceRequest(request, charge);
        return registerPaymentService.registerPayment(serviceRequest);
    }

    private static RegisterPaymentRequest buildServiceRequest(Request request, Charge charge) {
        return RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(request.method())
                .withCreditCard(request.creditCard())
                .build();
    }

    private void validateAccessToCharge(Charge charge) {
        if (!requesterProvider.requesterId().equals(charge.payerId())) {
            throw new AccessForbiddenException();
        }
    }

    public record Request(
            ChargeId chargeId,
            PaymentMethod method,
            CreditCard creditCard) {
    }
}
