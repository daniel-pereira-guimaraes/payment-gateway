package com.danielpg.paymentgateway.fixture;

import com.danielpg.paymentgateway.domain.user.Cpf;

public class CpfFixture {

    public static final String CPF1_VALUE = "00000000191";
    public static final String CPF2_VALUE = "99999999808";
    public static final Cpf CPF1 = Cpf.of(CPF1_VALUE);
    public static final Cpf CPF2 = Cpf.of(CPF2_VALUE);

    private CpfFixture() {
    }
}
