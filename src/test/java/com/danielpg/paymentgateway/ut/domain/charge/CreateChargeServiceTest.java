package com.danielpg.paymentgateway.ut.domain.charge;

import com.danielpg.paymentgateway.domain.AppClock;
import com.danielpg.paymentgateway.domain.PositiveMoney;
import com.danielpg.paymentgateway.domain.TimeMillis;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.charge.CreateChargeService;
import com.danielpg.paymentgateway.domain.user.Cpf;
import com.danielpg.paymentgateway.domain.user.User;
import com.danielpg.paymentgateway.domain.user.UserNotFoundException;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import com.danielpg.paymentgateway.fixture.CpfFixture;
import com.danielpg.paymentgateway.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.danielpg.paymentgateway.domain.user.UserNotFoundException.USER_WITH_CPF_NOT_FOUND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateChargeServiceTest {

    private static final Cpf ISSUER_CPF = CpfFixture.CPF1;
    private static final Cpf PAYER_CPF = CpfFixture.CPF2;
    private static final User ISSUER = UserFixture.builder().withCpf(ISSUER_CPF).build();
    private static final User PAYER = UserFixture.builder().withCpf(PAYER_CPF).build();
    private static final PositiveMoney AMOUNT = PositiveMoney.of(BigDecimal.TEN);
    private static final String DESCRIPTION = "Test charge";
    private static final TimeMillis NOW = TimeMillis.of(1_000_000L);
    private static final CreateChargeService.Request REQUEST =
            new CreateChargeService.Request(ISSUER_CPF, PAYER_CPF, AMOUNT, DESCRIPTION);

    private UserRepository userRepository;
    private ChargeRepository chargeRepository;
    private CreateChargeService service;

    @BeforeEach
    void setup() {
        var clock = mock(AppClock.class);
        when(clock.now()).thenReturn(NOW);
        userRepository = mock(UserRepository.class);
        chargeRepository = mock(ChargeRepository.class);
        service = new CreateChargeService(userRepository, chargeRepository, clock);
    }

    @Test
    void returnsChargeWhenAllDataIsValid() {
        when(userRepository.get(ISSUER_CPF)).thenReturn(java.util.Optional.of(ISSUER));
        when(userRepository.get(PAYER_CPF)).thenReturn(java.util.Optional.of(PAYER));

        var charge = service.createCharge(REQUEST);

        assertThat(charge, notNullValue());
        assertThat(charge.issuerId(), is(ISSUER.id()));
        assertThat(charge.payerId(), is(PAYER.id()));
        assertThat(charge.amount(), is(AMOUNT));
        assertThat(charge.description(), is(DESCRIPTION));
        assertThat(charge.createdAt(), is(NOW));
        assertThat(charge.dueAt(), is(NOW.plusDays(30)));
        verify(chargeRepository).save(charge);
    }

    @Test
    void throwsExceptionWhenIssuerNotFound() {
        when(userRepository.get(ISSUER_CPF)).thenReturn(java.util.Optional.empty());

        var exception = assertThrows(UserNotFoundException.class, () -> service.createCharge(REQUEST));

        assertThat(exception.getMessage(), is(USER_WITH_CPF_NOT_FOUND.formatted(ISSUER_CPF.value())));
        verifyNoInteractions(chargeRepository);
    }

    @Test
    void throwsExceptionWhenPayerNotFound() {
        when(userRepository.get(ISSUER_CPF)).thenReturn(java.util.Optional.of(ISSUER));
        when(userRepository.get(PAYER_CPF)).thenReturn(java.util.Optional.empty());

        var exception = assertThrows(UserNotFoundException.class, () -> service.createCharge(REQUEST));

        assertThat(exception.getMessage(), is(USER_WITH_CPF_NOT_FOUND.formatted(PAYER_CPF.value())));
        verifyNoInteractions(chargeRepository);
    }

    @Test
    void returnsChargeWithDueAtCalculatedFromCreatedAt() {
        when(userRepository.get(ISSUER_CPF)).thenReturn(java.util.Optional.of(ISSUER));
        when(userRepository.get(PAYER_CPF)).thenReturn(java.util.Optional.of(PAYER));

        var charge = service.createCharge(REQUEST);

        assertThat(charge.dueAt(), is(NOW.plusDays(30)));
    }
}