package com.danielpg.paymentgateway.it.infrastructure.controller.deposit;

import com.danielpg.paymentgateway.domain.charge.payment.PaymentAuthorizer;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentNotAuthorizedException;
import com.danielpg.paymentgateway.domain.deposit.DepositId;
import com.danielpg.paymentgateway.domain.deposit.DepositRepository;
import com.danielpg.paymentgateway.domain.user.UserId;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import com.danielpg.paymentgateway.it.infrastructure.controller.ControllerTestBase;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateDepositControllerTest extends ControllerTestBase {

    private static final String ENDPOINT = "/deposits";
    private static final BigDecimal VALID_AMOUNT = BigDecimal.TEN;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private PaymentAuthorizer paymentAuthorizer;

    @Test
    void createsDepositSuccessfully() throws Exception {
        var initialBalance = userBalance(CURRENT_USER.id());

        var responseJson = new JSONObject(
                mockMvc.perform(post(ENDPOINT)
                                .header(AUTHORIZATION, userToken())
                                .content("{\"amount\": " + VALID_AMOUNT + "}")
                                .contentType(APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString()
        );

        var depositId = responseJson.getLong("id");
        var deposit = depositRepository.get(DepositId.of(depositId)).orElseThrow();

        assertThat(deposit.id().value(), is(depositId));
        assertThat(deposit.userId().value(), is(CURRENT_USER.id().value()));
        assertThat(deposit.amount().value(), comparesEqualTo(VALID_AMOUNT));
        assertThat(deposit.createdAt().value(), is(clock.now().value()));
        assertThat(userBalance(CURRENT_USER.id()), comparesEqualTo(initialBalance.add(VALID_AMOUNT)));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(doubles = {0.0, -10.0})
    void returnsBadRequestForInvalidAmounts(Double invalidAmount) throws Exception {
        var content = invalidAmount == null ?  "{}" : "{\"amount\": " + invalidAmount + "}";
        var initialBalance = userBalance(CURRENT_USER.id());

        mockMvc.perform(post(ENDPOINT)
                        .header(AUTHORIZATION, userToken())
                        .content(content)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertThat(userBalance(CURRENT_USER.id()), comparesEqualTo(initialBalance));
    }


    @Test
    void returnsForbiddenWhenUnauthenticated() throws Exception {
        var initialBalance = userBalance(CURRENT_USER.id());

        mockMvc.perform(post(ENDPOINT)
                        .content("{\"amount\": " + VALID_AMOUNT + "}")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        assertThat(userBalance(CURRENT_USER.id()), comparesEqualTo(initialBalance));
    }

    @Test
    void returnsConflictWhenDepositNotAuthorized() throws Exception {
        var initialBalance = userBalance(CURRENT_USER.id());
        doThrow(PaymentNotAuthorizedException.class).when(paymentAuthorizer).authorizeDeposit(any());

        mockMvc.perform(post(ENDPOINT)
                        .header(AUTHORIZATION, userToken())
                        .content("{\"amount\": " + VALID_AMOUNT + "}")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict());

        assertThat(userBalance(CURRENT_USER.id()), comparesEqualTo(initialBalance));
    }

    private BigDecimal userBalance(UserId userId) {
        return userRepository.get(userId)
                .map(u -> u.balance().value())
                .orElse(null);
    }
}
