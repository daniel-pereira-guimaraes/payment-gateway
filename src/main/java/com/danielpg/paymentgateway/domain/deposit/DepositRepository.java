package com.danielpg.paymentgateway.domain.deposit;

import java.util.Optional;

public interface DepositRepository {
    Optional<Deposit> get(DepositId id);
    void save(Deposit deposit);
}
