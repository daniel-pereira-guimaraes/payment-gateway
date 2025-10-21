package com.danielpg.paymentgateway.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.charge.*;
import com.danielpg.paymentgateway.domain.user.UserId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcChargeRepository implements ChargeRepository {

    private static final String SQL_INSERT = """
            INSERT INTO tb_charge (issuer_id, payer_id, amount, description, created_at, due_at, status)
            VALUES (:issuerId, :payerId, :amount, :description, :createdAt, :dueAt, :status)
            """;

    private static final String SQL_UPDATE = """
            UPDATE tb_charge SET issuer_id = :issuerId, payer_id = :payerId, amount = :amount,
            description = :description, created_at = :createdAt, due_at = :dueAt, status = :status
            WHERE id = :id
            """;

    private static final String SQL_SELECT_BASE =
            "SELECT id, issuer_id, payer_id, amount, description, created_at, due_at, status FROM tb_charge";

    private static final String SQL_SELECT_BY_ID = SQL_SELECT_BASE + " WHERE id = :id";

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcChargeRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Charge> get(ChargeId id) {
        try {
            var params = Map.of("id", id.value());
            return Optional.ofNullable(jdbc.queryForObject(SQL_SELECT_BY_ID, params, (rs, rowNum) -> mapCharge(rs)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Charge getOrThrow(ChargeId id) {
        return get(id).orElseThrow(() -> new ChargeNotFoundException(id));
    }

    @Override
    public void save(Charge charge) {
        if (charge.id() == null) {
            var keyHolder = new CustomKeyHolder();
            jdbc.update(SQL_INSERT, commonParams(charge), keyHolder);
            charge.finalizeCreation(ChargeId.of(keyHolder.asLong()));
        } else {
            var params = commonParams(charge).addValue("id", charge.id().value());
            jdbc.update(SQL_UPDATE, params);
        }
    }

    private MapSqlParameterSource commonParams(Charge charge) {
        return new MapSqlParameterSource()
                .addValue("issuerId", charge.issuerId().value())
                .addValue("payerId", charge.payerId().value())
                .addValue("amount", charge.amount().value())
                .addValue("description", charge.description() == null ? null : charge.description().value())
                .addValue("createdAt", charge.createdAt().value())
                .addValue("dueAt", charge.dueAt().value())
                .addValue("status", charge.status().name());
    }

    private Charge mapCharge(java.sql.ResultSet rs) throws java.sql.SQLException {
        return Charge.builder()
                .withId(ChargeId.of(rs.getLong("id")))
                .withIssuerId(UserId.of(rs.getLong("issuer_id")))
                .withPayerId(UserId.of(rs.getLong("payer_id")))
                .withAmount(PositiveMoney.of(rs.getBigDecimal("amount")))
                .withDescription(ChargeDescription.ofNullable(rs.getString("description")).orElse(null))
                .withCreatedAt(TimeMillis.of(rs.getLong("created_at")))
                .withDueAt(TimeMillis.of(rs.getLong("due_at")))
                .withStatus(ChargeStatus.valueOf(rs.getString("status")))
                .build();
    }
}