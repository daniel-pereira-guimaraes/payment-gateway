package com.danielpg.paymentgateway.ut.infrastructure.security;

import com.danielpg.paymentgateway.domain.user.EmailAddress;
import com.danielpg.paymentgateway.domain.user.HashedPassword;
import com.danielpg.paymentgateway.domain.user.User;
import com.danielpg.paymentgateway.infrastructure.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDetailsImplTest {

    private User user;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        userDetails = new UserDetailsImpl(user);
    }

    @Test
    void returnsEmptyAuthoritiesWhenGetAuthoritiesIsCalled() {
        var authorities = userDetails.getAuthorities();

        assertThat(authorities, is(empty()));
    }

    @Test
    void returnsUserHashedPasswordWhenGetPasswordIsCalled() {
        var hashedPassword = mock(HashedPassword.class);
        when(user.hashedPassword()).thenReturn(hashedPassword);
        when(hashedPassword.hash()).thenReturn("hashed123");

        String result = userDetails.getPassword();

        assertThat(result, is("hashed123"));
    }

    @Test
    void returnsUserEmailWhenGetUsernameIsCalled() {
        var email = mock(EmailAddress.class);
        when(user.emailAddress()).thenReturn(email);
        when(email.value()).thenReturn("test@example.com");

        String result = userDetails.getUsername();

        assertThat(result, is("test@example.com"));
    }

    @Test
    void returnsTrueWhenIsAccountNonExpiredIsCalled() {
        assertThat(userDetails.isAccountNonExpired(), is(true));
    }

    @Test
    void returnsTrueWhenIsAccountNonLockedIsCalled() {
        assertThat(userDetails.isAccountNonLocked(), is(true));
    }

    @Test
    void returnsTrueWhenIsCredentialsNonExpiredIsCalled() {
        assertThat(userDetails.isCredentialsNonExpired(), is(true));
    }

    @Test
    void returnsTrueWhenIsEnabledIsCalled() {
        assertThat(userDetails.isEnabled(), is(true));
    }
}