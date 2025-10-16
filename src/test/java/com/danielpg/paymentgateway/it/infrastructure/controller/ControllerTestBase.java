package com.danielpg.paymentgateway.it.infrastructure.controller;


import com.danielpg.paymentgateway.it.infrastructure.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public abstract class ControllerTestBase extends IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;
}
