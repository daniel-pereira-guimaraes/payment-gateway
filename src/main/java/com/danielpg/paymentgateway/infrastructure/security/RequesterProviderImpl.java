package com.danielpg.paymentgateway.infrastructure.security;

import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.user.User;
import com.danielpg.paymentgateway.domain.user.UserId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class RequesterProviderImpl implements RequesterProvider {

    @Override
    public User requester() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return ((UserDetailsImpl) authentication.getPrincipal()).user();
        }
        throw new SecurityException("Usuário não autenticado.");
    }

    @Override
    public UserId requesterId() {
        return requester().id();
    }
}
