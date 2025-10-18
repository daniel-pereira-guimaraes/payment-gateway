package com.danielpg.paymentgateway.it.infrastructure.controller.user;

import com.danielpg.paymentgateway.domain.user.UserId;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import com.danielpg.paymentgateway.it.infrastructure.controller.ControllerTestBase;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CreateUserControllerTest extends ControllerTestBase {

    private static final String ENDPOINT = "/users";

    private static final String NAME = "New User";
    private static final String CPF = "00000000272";
    private static final String EMAIL = "new@server.com";
    private static final String PASSWORD = "Password!321";

    private static final String REQUEST_BODY_PATTERN = """
            {
                "name": "%s",
                "cpf": "%s",
                "emailAddress": "%s",
                "password": "%s"
            }
            """;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUserReturnsCreatedUser() throws Exception {
        var requestBody = String.format(REQUEST_BODY_PATTERN, NAME, CPF, EMAIL, PASSWORD);

        var response = mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.cpf", is(CPF)))
                .andExpect(jsonPath("$.emailAddress", is(EMAIL)))
                .andReturn().getResponse().getContentAsString();

        var id = new JSONObject(response).getLong("id");
        var savedUser = userRepository.get(UserId.of(id)).orElseThrow();
        assert savedUser.name().value().equals(NAME);
        assert savedUser.cpf().value().equals(CPF);
        assert savedUser.emailAddress().value().equals(EMAIL);
    }
}
