package com.danielpg.paymentgateway.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.deposit.Deposit;
import com.danielpg.paymentgateway.domain.deposit.DepositId;
import com.danielpg.paymentgateway.domain.deposit.DepositRepository;
import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.user.UserId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcDepositRepository implements DepositRepository {

    private static final String SQL_INSERT = """
            INSERT INTO tb_deposit (user_id, amount, created_at)
            VALUES (:userId, :amount, :createdAt)
            """;

    private static final String SQL_UPDATE = """
            UPDATE tb_deposit SET user_id = :userId, amount = :amount, created_at = :createdAt
            WHERE id = :id
            """;

    private static final String SQL_SELECT_BY_ID = """
            SELECT id, user_id, amount, created_at FROM tb_deposit WHERE id = :id
            """;

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcDepositRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Deposit> get(DepositId id) {
        try {
            var params = Map.of("id", id.value());
            return Optional.ofNullable(jdbc.queryForObject(SQL_SELECT_BY_ID, params, (rs, rowNum) -> mapDeposit(rs)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(Deposit deposit) {
        if (deposit.id() == null) {
            var keyHolder = new CustomKeyHolder();
            jdbc.update(SQL_INSERT, commonParams(deposit), keyHolder);
            deposit.finalizeCreation(DepositId.of(keyHolder.asLong()));
        } else {
            var params = commonParams(deposit).addValue("id", deposit.id().value());
            jdbc.update(SQL_UPDATE, params);
        }
    }

    private MapSqlParameterSource commonParams(Deposit deposit) {
        return new MapSqlParameterSource()
                .addValue("userId", deposit.userId().value())
                .addValue("amount", deposit.amount().value())
                .addValue("createdAt", deposit.createdAt().value());
    }

    private Deposit mapDeposit(java.sql.ResultSet rs) throws java.sql.SQLException {
        return Deposit.builder()
                .withId(DepositId.of(rs.getLong("id")))
                .withUserId(UserId.of(rs.getLong("user_id")))
                .withAmount(PositiveMoney.of(rs.getBigDecimal("amount")))
                .withCreatedAt(TimeMillis.of(rs.getLong("created_at")))
                .build();
    }
}
