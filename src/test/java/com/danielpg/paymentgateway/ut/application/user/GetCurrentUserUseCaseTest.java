package com.danielpg.paymentgateway.ut.application.user;

import com.danielpg.paymentgateway.application.shared.RequesterProvider;
import com.danielpg.paymentgateway.application.user.GetCurrentUserUseCase;
import com.danielpg.paymentgateway.domain.user.User;
import com.danielpg.paymentgateway.domain.user.UserRepository;
import com.danielpg.paymentgateway.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class GetCurrentUserUseCaseTest {

    private RequesterProvider requesterProvider;
    private UserRepository userRepository;
    private GetCurrentUserUseCase useCase;
    private User user;

    @BeforeEach
    void setUp() {
        requesterProvider = mock(RequesterProvider.class);
        userRepository = mock(UserRepository.class);
        useCase = new GetCurrentUserUseCase(requesterProvider, userRepository);
        user = UserFixture.builder().build();
        when(requesterProvider.requesterId()).thenReturn(UserFixture.USER_ID);
    }

    @Test
    void getCurrentUserSuccessfully() {
        when(userRepository.getOrThrow(UserFixture.USER_ID)).thenReturn(user);

        var result = useCase.getCurrentUser();

        assertThat(result, is(user));
        verify(requesterProvider).requesterId();
        verify(userRepository).getOrThrow(UserFixture.USER_ID);
    }

    @Test
    void throwsExceptionWhenRequesterProviderFails() {
        when(requesterProvider.requesterId()).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.getCurrentUser());

        verify(requesterProvider).requesterId();
        verifyNoInteractions(userRepository);
    }

    @Test
    void throwsExceptionWhenUserRepositoryFails() {
        when(userRepository.getOrThrow(UserFixture.USER_ID)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.getCurrentUser());

        verify(requesterProvider).requesterId();
        verify(userRepository).getOrThrow(UserFixture.USER_ID);
    }
}
