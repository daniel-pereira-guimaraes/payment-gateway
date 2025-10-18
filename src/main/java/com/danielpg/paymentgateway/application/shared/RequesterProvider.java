package com.danielpg.paymentgateway.application.shared;

import com.danielpg.paymentgateway.domain.user.User;
import com.danielpg.paymentgateway.domain.user.UserId;

public interface RequesterProvider {
    User requester();
    UserId requesterId();
}
