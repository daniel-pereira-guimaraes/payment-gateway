package com.danielpg.paymentgateway.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.user.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {

    private static final String SQL_INSERT = """
            INSERT INTO tb_user (name, cpf, email_address, hashed_password, balance)
            VALUES (:name, :cpf, :email_address, :hashedPassword, :balance)
            """;

    private static final String SQL_UPDATE = """
            UPDATE tb_user SET name = :name, cpf = :cpf, email_address = :email_address,
            hashed_password = :hashedPassword, balance = :balance WHERE id = :id
            """;

    private static final String SQL_SELECT_BASE =
            "SELECT id, name, cpf, email_address, hashed_password, balance FROM tb_user";

    private static final String SQL_SELECT_BY_ID = SQL_SELECT_BASE + " WHERE id = :id";
    private static final String SQL_SELECT_BY_CPF = SQL_SELECT_BASE + " WHERE cpf = :cpf";
    private static final String SQL_SELECT_BY_EMAIL = SQL_SELECT_BASE + " WHERE email_address = :email_address";

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcUserRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<User> get(Cpf cpf) {
        return queryForOptional(SQL_SELECT_BY_CPF, Map.of("cpf", cpf.value()));
    }

    @Override
    public Optional<User> get(UserId id) {
        return queryForOptional(SQL_SELECT_BY_ID, Map.of("id", id.value()));
    }

    @Override
    public User getOrThrow(UserId id) {
        return get(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User getOrThrow(Cpf cpf) {
        return get(cpf).orElseThrow(() -> new UserNotFoundException(cpf));
    }

    @Override
    public User getOrThrow(EmailAddress emailAddress) {
        var params = Map.of("email_address", emailAddress.value());
        return queryForOptional(SQL_SELECT_BY_EMAIL, params)
                .orElseThrow(() -> new UserNotFoundException(emailAddress));
    }

    @Override
    public void save(User user) {
        if (user.id() == null) {
            var keyHolder = new CustomKeyHolder();
            jdbc.update(SQL_INSERT, commonParams(user), keyHolder);
            user.finalizeCreation(UserId.of(keyHolder.asLong()));
        } else {
            var params = commonParams(user).addValue("id", user.id().value());
            jdbc.update(SQL_UPDATE, params);
        }
    }

    private Optional<User> queryForOptional(String sql, Map<String, ?> params) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, params, (rs, rowNum) -> mapUser(rs)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private MapSqlParameterSource commonParams(User user) {
        return new MapSqlParameterSource()
                .addValue("name", user.name().value())
                .addValue("cpf", user.cpf().value())
                .addValue("email_address", user.emailAddress().value())
                .addValue("hashedPassword", user.hashedPassword().hash())
                .addValue("balance", user.balance().value());
    }

    private User mapUser(java.sql.ResultSet rs) throws java.sql.SQLException {
        return User.builder()
                .withId(UserId.of(rs.getLong("id")))
                .withName(PersonName.of(rs.getString("name")))
                .withCpf(Cpf.of(rs.getString("cpf")))
                .withEmailAddress(EmailAddress.of(rs.getString("email_address")))
                .withHashedPassword(HashedPassword.of(rs.getString("hashed_password")))
                .withBalance(Balance.of(rs.getBigDecimal("balance")))
                .build();
    }
}
