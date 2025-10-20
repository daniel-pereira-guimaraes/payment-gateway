package com.danielpg.paymentgateway.it.infrastructure.controller.charge;

import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentAuthorizer;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentNotAuthorizedException;
import com.danielpg.paymentgateway.domain.user.UserId;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.it.infrastructure.controller.ControllerTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CancelChargeControllerTest extends ControllerTestBase {

    private static final String ENDPOINT_TEMPLATE = "/charges/%d/cancel";

    private static final long CHARGE_ID_PENDING = 1L;       // emitente=1
    private static final long CHARGE_ID_PAID_BALANCE = 5L;  // emitente=1
    private static final long CHARGE_ID_PAID_CARD = 2L;     // emitente=1
    private static final long CHARGE_ID_CANCELED = 7L;      // emitente=1
    private static final long CHARGE_ID_FORBIDDEN = 4L;     // emitente=2

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChargeRepository chargeRepository;

    @MockBean
    private PaymentAuthorizer paymentAuthorizer;

    @Test
    void cancelsPendingChargeSuccessfully() throws Exception {
        mockMvc.perform(patch(ENDPOINT_TEMPLATE.formatted(CHARGE_ID_PENDING))
                        .header("Authorization", userToken()))
                .andExpect(status().isNoContent());

        var charge = chargeRepository.getOrThrow(ChargeId.of(CHARGE_ID_PENDING));
        assertThat(charge.status(), is(ChargeStatus.CANCELED));
    }

    @Test
    void cancelsPaidChargeWithBalanceSuccessfully() throws Exception {
        mockMvc.perform(patch(ENDPOINT_TEMPLATE.formatted(CHARGE_ID_PAID_BALANCE))
                        .header("Authorization", userToken()))
                .andExpect(status().isNoContent());

        var charge = chargeRepository.getOrThrow(ChargeId.of(CHARGE_ID_PAID_BALANCE));
        assertThat(charge.status(), is(ChargeStatus.CANCELED));

        var issuer = userRepository.getOrThrow(UserId.of(1L));
        var payer = userRepository.getOrThrow(UserId.of(2L));
        assertThat(payer.balance().value(), is(new BigDecimal("5300.00"))); // +300
        assertThat(issuer.balance().value(), is(new BigDecimal("700.00"))); // -300
    }

    @Test
    void cancelsPaidChargeWithCreditCardAndCallsAuthorizer() throws Exception {
        mockMvc.perform(patch(ENDPOINT_TEMPLATE.formatted(CHARGE_ID_PAID_CARD))
                        .header("Authorization", userToken()))
                .andExpect(status().isNoContent());

        var charge = chargeRepository.getOrThrow(ChargeId.of(CHARGE_ID_PAID_CARD));
        assertThat(charge.status(), is(ChargeStatus.CANCELED));

        verify(paymentAuthorizer).authorizeCancellation(any(), any());
    }

    @Test
    void failsWhenCreditCardCancellationNotAuthorized() throws Exception {
        doThrow(PaymentNotAuthorizedException.class)
                .when(paymentAuthorizer).authorizeCancellation(any(), any());

        mockMvc.perform(patch(ENDPOINT_TEMPLATE.formatted(CHARGE_ID_PAID_CARD))
                        .header("Authorization", userToken()))
                .andExpect(status().isConflict());

        var charge = chargeRepository.getOrThrow(ChargeId.of(CHARGE_ID_PAID_CARD));
        assertThat(charge.status(), is(ChargeStatus.PAID));
    }

    @Test
    void failsWhenChargeAlreadyCanceled() throws Exception {
        mockMvc.perform(patch(ENDPOINT_TEMPLATE.formatted(CHARGE_ID_CANCELED))
                        .header("Authorization", userToken()))
                .andExpect(status().isConflict());

        var charge = chargeRepository.getOrThrow(ChargeId.of(CHARGE_ID_CANCELED));
        assertThat(charge.status(), is(ChargeStatus.CANCELED));
    }

    @Test
    void failsWhenChargeDoesNotExist() throws Exception {
        var nonExistentChargeId = 999L;
        mockMvc.perform(patch(ENDPOINT_TEMPLATE.formatted(nonExistentChargeId))
                        .header("Authorization", userToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void failsWhenUserNotAuthenticated() throws Exception {
        mockMvc.perform(patch(ENDPOINT_TEMPLATE.formatted(CHARGE_ID_PENDING)))
                .andExpect(status().isForbidden());
    }

    @Test
    void failsWhenRequesterIsNotIssuer() throws Exception {
        mockMvc.perform(patch(ENDPOINT_TEMPLATE.formatted(CHARGE_ID_FORBIDDEN))
                        .header("Authorization", userToken()))
                .andExpect(status().isForbidden());
    }
}
