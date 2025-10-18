package com.danielpg.paymentgateway.application.auth;

import com.danielpg.paymentgateway.domain.user.User;

public interface AppTokenService {
    Token generate(User user);
    Token decode(String rawToken);
}
