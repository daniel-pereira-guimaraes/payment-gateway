package com.danielpg.paymentgateway.it.infrastructure.controller.user;

import com.danielpg.paymentgateway.it.infrastructure.controller.ControllerTestBase;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetCurrentUserControllerTest extends ControllerTestBase {

    private static final String ENDPOINT = "/users/me";

    @Test
    void returnsCurrentUserSuccessfullyWhenAuthenticated() throws Exception {
        var response = new JSONObject(
                mockMvc.perform(get(ENDPOINT)
                                .header(AUTHORIZATION, userToken())
                                .contentType(APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );

        assertThat(response.getLong("id"), is(CURRENT_USER.id().value()));
        assertThat(response.getString("name"), is(CURRENT_USER.name().value()));
        assertThat(response.getString("cpf"), is(CURRENT_USER.cpf().value()));
        assertThat(response.getString("emailAddress"), is(CURRENT_USER.emailAddress().value()));
        assertThat(new BigDecimal(response.getString("balance")), comparesEqualTo(CURRENT_USER.balance().value()));
    }

    @Test
    void returnsUnauthorizedWhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
