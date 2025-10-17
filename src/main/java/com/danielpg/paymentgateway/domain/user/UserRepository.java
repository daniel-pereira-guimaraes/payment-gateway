package com.danielpg.paymentgateway.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> get(Cpf cpf);
    User getOrThrow(UserId id);
    void save(User user);
}
