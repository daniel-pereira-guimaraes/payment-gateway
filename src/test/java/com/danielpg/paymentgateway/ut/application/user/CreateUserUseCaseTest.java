package com.danielpg.paymentgateway.ut.application.user;

import static com.danielpg.paymentgateway.fixture.AppTransactionFixture.assertThatInTransaction;
import static com.danielpg.paymentgateway.fixture.UserFixture.HASHED_PASSWORD;
import static com.danielpg.paymentgateway.fixture.UserFixture.PLAIN_TEXT_PASSWORD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.danielpg.paymentgateway.fixture.AppTransactionFixture;
import com.danielpg.paymentgateway.fixture.UserFixture;

import com.danielpg.paymentgateway.application.user.CreateUserUseCase;
import com.danielpg.paymentgateway.domain.user.*;
class CreateUserUseCaseTest {

    private UserRepository repository;
    private PasswordHasher passwordHasher;
    private CreateUserUseCase useCase;
    private CreateUserUseCase.Request request;
    private User expected;

    @BeforeEach
    void beforeEach() {
        var transaction = AppTransactionFixture.mockedTransaction();

        repository = mock(UserRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        useCase = new CreateUserUseCase(transaction, repository, passwordHasher);

        expected = UserFixture.builder()
                .withId(null)
                .withHashedPassword(HASHED_PASSWORD)
                .withBalance(Balance.ZERO)
                .build();

        request = new CreateUserUseCase.Request(
                expected.name(),
                expected.cpf(),
                expected.emailAddress(),
                PLAIN_TEXT_PASSWORD
        );

        assertThatInTransaction(transaction).when(repository).save(any());
        when(passwordHasher.hashedPassword(PLAIN_TEXT_PASSWORD)).thenReturn(HASHED_PASSWORD);
    }

    @Test
    void createsUserSuccessfully() {
        var result = useCase.createUser(request);

        assertThat(result, is(expected));
        verify(passwordHasher).hashedPassword(PLAIN_TEXT_PASSWORD);
        verify(repository).save(result);
    }

    @Test
    void propagatesExceptionWhenPasswordHasherFails() {
        when(passwordHasher.hashedPassword(PLAIN_TEXT_PASSWORD)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.createUser(request));

        verify(repository, never()).save(any());
    }

    @Test
    void propagatesExceptionWhenRepositoryFails() {
        doThrow(RuntimeException.class).when(repository).save(any(User.class));

        assertThrows(RuntimeException.class, () -> useCase.createUser(request));

        verify(repository).save(any(User.class));
    }
}
