package com.danielpg.paymentgateway.domain.user;

public interface UserRepository {
    User get(Cpf cpf);
    void save(User user);
}
