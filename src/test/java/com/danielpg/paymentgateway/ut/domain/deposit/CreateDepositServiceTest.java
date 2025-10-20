package com.danielpg.paymentgateway.ut.domain.deposit;

import com.danielpg.paymentgateway.domain.charge.payment.PaymentAuthorizer;
import com.danielpg.paymentgateway.domain.deposit.CreateDepositService;
import com.danielpg.paymentgateway.domain.deposit.DepositRepository;
import com.danielpg.paymentgateway.domain.deposit.DepositRequest;
import com.danielpg.paymentgateway.domain.shared.AppClock;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.user.Balance;
import com.danielpg.paymentgateway.domain.user.User;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import com.danielpg.paymentgateway.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.danielpg.paymentgateway.fixture.UserFixture.USER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CreateDepositServiceTest {

    private static final TimeMillis NOW = TimeMillis.of(999L);
    private static final PositiveMoney DEPOSIT_VALUE = PositiveMoney.of(new BigDecimal("10.00"));
    private static final Balance INITIAL_BALANCE = Balance.of(new BigDecimal("2.00"));
    private static final Balance FINAL_BALANCE = Balance.of(new BigDecimal("12.00"));
    private static final DepositRequest REQUEST = DepositRequest.of(USER_ID, DEPOSIT_VALUE);

    private DepositRepository depositRepository;
    private UserRepository userRepository;
    private PaymentAuthorizer authorizer;
    private CreateDepositService service;
    private User user;

    @BeforeEach
    void setup() {
        depositRepository = mock(DepositRepository.class);
        userRepository = mock(UserRepository.class);
        authorizer = mock(PaymentAuthorizer.class);
        user = UserFixture.builder().withId(USER_ID).withBalance(INITIAL_BALANCE).build();
        var clock = mock(AppClock.class);

        service = new CreateDepositService(depositRepository, userRepository, authorizer, clock);

        when(userRepository.getOrThrow(USER_ID)).thenReturn(user);
        when(clock.now()).thenReturn(NOW);
    }

    @Test
    void createsDepositAndUpdatesBalanceWhenAuthorized() {
        var deposit = service.createDeposit(REQUEST);

        assertThat(deposit.userId(), is(USER_ID));
        assertThat(deposit.amount(), is(DEPOSIT_VALUE));
        assertThat(deposit.createdAt(), is(NOW));
        assertThat(user.balance(), is(FINAL_BALANCE));

        verify(authorizer).authorizeDeposit(deposit);
        verify(userRepository).save(user);
        verify(depositRepository).save(deposit);
    }

    @Test
    void throwsExceptionWhenAuthorizationFails() {
        doThrow(new RuntimeException("Depósito não autorizado pelo serviço externo."))
                .when(authorizer).authorizeDeposit(any());

        var ex = assertThrows(RuntimeException.class, () -> service.createDeposit(REQUEST));

        assertThat(ex.getMessage(), is("Depósito não autorizado pelo serviço externo."));
        verify(depositRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void throwsExceptionWhenDepositRepositoryFails() {
        doThrow(new RuntimeException("Falha ao salvar depósito"))
                .when(depositRepository).save(any());

        var ex = assertThrows(RuntimeException.class,
                () -> service.createDeposit(REQUEST)
        );

        assertThat(ex.getMessage(), is("Falha ao salvar depósito"));
        verify(userRepository, never()).save(any());
    }

}
