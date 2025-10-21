package com.danielpg.paymentgateway.application.user;

import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.domain.user.User;
import com.danielpg.paymentgateway.domain.user.UserRepository;

public class GetCurrentUserUseCase {

    private final RequesterProvider requesterProvider;
    private final UserRepository repository;

    public GetCurrentUserUseCase(RequesterProvider requesterProvider, UserRepository repository) {
        this.requesterProvider = requesterProvider;
        this.repository = repository;
    }

    public User getCurrentUser() {
        var requesterId = requesterProvider.requesterId();
        return repository.getOrThrow(requesterId);
    }
}
