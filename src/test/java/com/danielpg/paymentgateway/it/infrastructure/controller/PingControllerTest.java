package com.danielpg.paymentgateway.it.infrastructure.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PingControllerTest extends ControllerTestBase {

    private static final String PING_ENDPOINT = "/ping";

    @Test
    void test() throws Exception {
        mockMvc.perform(get(PING_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

}
