package com.danielpg.paymentgateway.it.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.user.*;
import com.danielpg.paymentgateway.it.infrastructure.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.danielpg.paymentgateway.fixture.UserFixture.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class JdbcUserRepositoryTest extends IntegrationTestBase {

    @Autowired
    private UserRepository repository;

    @Test
    void getByIdReturnsUserWhenFound() {
        var user = builder().withId(null).build();
        repository.save(user);

        var result = repository.get(user.id()).orElseThrow();
        assertThat(result, is(user));
    }

    @Test
    void getOrThrowByCpfReturnsUserWhenFound() {
        var user = builder().withId(null).build();
        repository.save(user);

        var result = repository.getOrThrow(user.cpf());
        assertThat(result, is(user));
    }

    @Test
    void getOrThrowByEmailReturnsUserWhenFound() {
        var user = builder().withId(null).build();
        repository.save(user);

        var result = repository.getOrThrow(user.emailAddress());
        assertThat(result, is(user));
    }

    @Test
    void getByIdReturnsEmptyWhenNotFound() {
        var id = UserId.of(999L);

        var user = repository.get(id);

        assertThat(user.isEmpty(), is(true));
    }

    @Test
    void getOrThrowByIdThrowsExceptionWhenNotFound() {
        var id = UserId.of(999L);

        var exception = assertThrows(UserNotFoundException.class,
                () -> repository.getOrThrow(id)
        );

        assertThat(exception.getMessage(), is("Usuário com ID 999 não encontrado."));
    }

    @Test
    void getOrThrowByEmailThrowsExceptionWhenNotFound() {
        var emailAddress = EmailAddress.of("x@y.com");

        var exception = assertThrows(UserNotFoundException.class,
                () -> repository.getOrThrow(emailAddress)
        );

        assertThat(exception.getMessage(), is("Usuário com email x@y.com não encontrado."));
    }

    @Test
    void getByCpfReturnsUserWhenFound() {
        var user = builder().withId(null).build();

        repository.save(user);

        var found = repository.get(user.cpf()).orElseThrow();
        assertThat(found.cpf(), is(user.cpf()));
    }

    @Test
    void getByCpfReturnsEmptyWhenNotFound() {
        var cpf = Cpf.of("12345678909");

        var user = repository.get(cpf);

        assertThat(user.isEmpty(), is(true));
    }

    @Test
    void mustAddAndGetById() {
        var user = builder().withId(null).build();

        repository.save(user);

        var retrieved = repository.get(user.id()).orElseThrow();
        assertThat(user.id(), notNullValue());
        assertThat(user, is(retrieved));
    }

    @Test
    void mustUpdateExistingUser() {
        var user = builder().withId(null).build();
        repository.save(user);

        var updated = builder().withId(user.id()).withName(PersonName.of("Updated Name")).build();
        repository.save(updated);

        var reloaded = repository.get(user.id()).orElseThrow();
        assertThat(reloaded.name().value(), is("Updated Name"));
    }
}
