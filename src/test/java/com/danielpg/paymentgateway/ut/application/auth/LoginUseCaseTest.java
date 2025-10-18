package com.danielpg.paymentgateway.ut.application.auth;

import com.danielpg.paymentgateway.application.auth.*;
import com.danielpg.paymentgateway.domain.user.*;
import com.danielpg.paymentgateway.fixture.TokenFixture;
import com.danielpg.paymentgateway.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.danielpg.paymentgateway.fixture.UserFixture.PLAIN_TEXT_PASSWORD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LoginUseCaseTest {

    private UserRepository userRepository;
    private AppTokenService tokenService;
    private PasswordHasher passwordHasher;
    private LoginUseCase useCase;

    private User user;
    private Token token;
    private LoginUseCase.Request requestWithEmail;
    private LoginUseCase.Request requestWithCpf;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        tokenService = mock(AppTokenService.class);
        passwordHasher = mock(PasswordHasher.class);

        useCase = new LoginUseCase(userRepository, tokenService, passwordHasher);

        user = UserFixture.builder().build();
        token = TokenFixture.builder().build();
        requestWithEmail = new LoginUseCase.Request(user.emailAddress(), null, PLAIN_TEXT_PASSWORD);
        requestWithCpf = new LoginUseCase.Request(null, user.cpf(), PLAIN_TEXT_PASSWORD);
    }

    @Test
    void logsInSuccessfullyWithEmail() {
        when(userRepository.getOrThrow(user.emailAddress())).thenReturn(user);
        when(passwordHasher.matches(PLAIN_TEXT_PASSWORD, user.hashedPassword())).thenReturn(true);
        when(tokenService.generate(user)).thenReturn(token);

        var result = useCase.login(requestWithEmail);

        assertThat(result, is(token));
        verify(userRepository).getOrThrow(user.emailAddress());
        verify(passwordHasher).matches(PLAIN_TEXT_PASSWORD, user.hashedPassword());
        verify(tokenService).generate(user);
    }

    @Test
    void logsInSuccessfullyWithCpf() {
        when(userRepository.getOrThrow(user.cpf())).thenReturn(user);
        when(passwordHasher.matches(PLAIN_TEXT_PASSWORD, user.hashedPassword())).thenReturn(true);
        when(tokenService.generate(user)).thenReturn(token);

        var result = useCase.login(requestWithCpf);

        assertThat(result, is(token));
        verify(userRepository).getOrThrow(user.cpf());
        verify(passwordHasher).matches(PLAIN_TEXT_PASSWORD, user.hashedPassword());
        verify(tokenService).generate(user);
    }

    @Test
    void throwsInvalidCredentialsWhenPasswordDoesNotMatch() {
        when(userRepository.getOrThrow(user.emailAddress())).thenReturn(user);
        when(passwordHasher.matches(PLAIN_TEXT_PASSWORD, user.hashedPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> useCase.login(requestWithEmail));

        verify(tokenService, never()).generate(any());
    }

    @Test
    void loginByEmailPropagatesUserNotFoundException() {
        when(userRepository.getOrThrow(user.emailAddress())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> useCase.login(requestWithEmail));

        verify(passwordHasher, never()).matches(any(), any());
        verify(tokenService, never()).generate(any());
    }

    @Test
    void loginByCpfPropagatesUserNotFoundException() {
        when(userRepository.getOrThrow(user.cpf())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> useCase.login(requestWithCpf));

        verify(passwordHasher, never()).matches(any(), any());
        verify(tokenService, never()).generate(any());
    }

    @Test
    void throwsInvalidCredentialsWhenUnexpectedErrorOccurs() {
        when(userRepository.getOrThrow(user.emailAddress())).thenReturn(user);
        when(passwordHasher.matches(any(), any())).thenThrow(RuntimeException.class);

        assertThrows(InvalidCredentialsException.class, () -> useCase.login(requestWithEmail));

        verify(tokenService, never()).generate(any());
    }

    @Test
    void throwsIllegalArgumentExceptionWhenNoEmailOrCpfProvided() {
        var request = new LoginUseCase.Request(null, null, PLAIN_TEXT_PASSWORD);

        var exception = assertThrows(IllegalArgumentException.class, () -> useCase.login(request));

        assertThat(exception.getMessage(), is("Obrigatório informar CPF ou e-mail."));
        verifyNoInteractions(userRepository, passwordHasher, tokenService);
    }

    @Test
    void throwsInvalidCredentialsWhenTokenGenerationFails() {
        when(userRepository.getOrThrow(user.emailAddress())).thenReturn(user);
        when(passwordHasher.matches(PLAIN_TEXT_PASSWORD, user.hashedPassword())).thenReturn(true);
        when(tokenService.generate(user)).thenThrow(RuntimeException.class);

        assertThrows(InvalidCredentialsException.class, () -> useCase.login(requestWithEmail));

        verify(tokenService).generate(user);
    }
}
