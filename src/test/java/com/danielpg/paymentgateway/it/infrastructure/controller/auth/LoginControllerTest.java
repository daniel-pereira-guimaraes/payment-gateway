package com.danielpg.paymentgateway.it.infrastructure.controller.auth;

import com.danielpg.paymentgateway.domain.user.UserRepository;
import com.danielpg.paymentgateway.infrastructure.security.JwtTokenService;
import com.danielpg.paymentgateway.it.infrastructure.controller.ControllerTestBase;
import io.micrometer.common.util.StringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class LoginControllerTest extends ControllerTestBase {

    private static final String ENDPOINT = "/auth/login";

    private static final String EMAIL = "joao.silva@email.com";
    private static final String CPF = "12312312387";
    private static final String PASSWORD = "Password!12345";
    private static final String WRONG_PASSWORD = "WrongPass!999";
    private static final String UNKNOWN_EMAIL = "unknown@email.com";
    private static final String UNKNOWN_CPF = "00000000353";
    private static final String INVALID_EMAIL = "invalid-email";
    private static final String INVALID_CPF = "123";

    private static final String REQUEST_BODY = """
            {
                "cpf": "%s",
                "emailAddress": "%s",
                "password": "%s"
            }
            """;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Test
    void loginSuccessfullyWithEmail() throws Exception {
        var requestBody = buildRequestBody(EMAIL, null, PASSWORD);

        var response = mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var token = new JSONObject(response).getString("token");
        var decoded = jwtTokenService.decode(token);

        assertThat(decoded.rawToken(), notNullValue());
        assertThat(decoded.isExpired(), org.hamcrest.Matchers.is(false));
        assertThat(decoded.user().emailAddress().value(), org.hamcrest.Matchers.is(EMAIL));
    }

    @Test
    void loginSuccessfullyWithCpf() throws Exception {
        var requestBody = buildRequestBody(null, CPF, PASSWORD);

        var response = mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var token = new JSONObject(response).getString("token");
        var decoded = jwtTokenService.decode(token);

        assertThat(decoded.isExpired(), org.hamcrest.Matchers.is(false));
        assertThat(decoded.user().cpf().value(), org.hamcrest.Matchers.is(CPF));
    }

    @Test
    void returnsNotFoundWhenEmailNotFound() throws Exception {
        var requestBody = buildRequestBody(UNKNOWN_EMAIL, null, PASSWORD);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsNotFoundWhenCpfNotFound() throws Exception {
        var requestBody = buildRequestBody(null, UNKNOWN_CPF, PASSWORD);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsUnauthorizedWhenPasswordDoesNotMatch() throws Exception {
        var requestBody = buildRequestBody(EMAIL, null, WRONG_PASSWORD);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnsBadRequestWhenMissingPassword() throws Exception {
        var requestBody = buildRequestBody(EMAIL, null, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsBadRequestWhenMissingEmailAndCpf() throws Exception {
        var requestBody = buildRequestBody(null, null, PASSWORD);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsBadRequestWhenEmailIsInvalid() throws Exception {
        var requestBody = buildRequestBody(INVALID_EMAIL, null, PASSWORD);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsBadRequestWhenCpfIsInvalid() throws Exception {
        var requestBody = buildRequestBody(null, INVALID_CPF, PASSWORD);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    private String buildRequestBody(String email, String cpf, String password) {
        var nonNullPassword = password != null ? password : "";
        if (!StringUtils.isBlank(cpf)) {
            return REQUEST_BODY.formatted(cpf, "", nonNullPassword);
        }
        if (!StringUtils.isBlank(email)) {
            return REQUEST_BODY.formatted("", email, nonNullPassword);
        }
        return REQUEST_BODY.formatted("", "", nonNullPassword);
    }
}
