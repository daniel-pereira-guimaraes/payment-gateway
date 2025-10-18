package com.danielpg.paymentgateway.application.shared;

public interface AppTransaction {
    boolean inTransaction();
    void execute(Runnable runnable);
}
