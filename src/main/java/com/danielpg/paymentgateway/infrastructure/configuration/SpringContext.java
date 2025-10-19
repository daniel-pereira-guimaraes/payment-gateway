package com.danielpg.paymentgateway.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpringContext {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
