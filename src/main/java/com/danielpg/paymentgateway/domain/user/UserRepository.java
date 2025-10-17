package com.danielpg.paymentgateway.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> get(Cpf cpf);
    void save(User user);
}
