package com.danielpg.paymentgateway.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesFilter;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesItem;
import com.danielpg.paymentgateway.domain.charge.query.received.ReceivedChargesQuery;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class JdbcReceivedChargesQuery implements ReceivedChargesQuery {

    private static final String SQL_BASE = """
            SELECT
                c.id,
                issuer.cpf AS issuer_cpf,
                issuer.name AS issuer_name,
                c.amount,
                c.description,
                c.created_at,
                c.due_at,
                p.paid_at,
                c.status
            FROM tb_charge c
            JOIN tb_user issuer ON c.issuer_id = issuer.id
            LEFT JOIN tb_payment p ON p.charge_id = c.id
            WHERE c.payer_id = :payerId
            """;

    private static final String SQL_ORDER = " ORDER BY c.id";

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<ReceivedChargesItem> mapper;

    public JdbcReceivedChargesQuery(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.mapper = createMapper();
    }

    @Override
    public List<ReceivedChargesItem> execute(ReceivedChargesFilter filter) {
        var sql = buildSql(filter);
        var params = buildParams(filter);
        return jdbc.query(sql, params, mapper);
    }

    private String buildSql(ReceivedChargesFilter filter) {
        var sql = new StringBuilder(SQL_BASE);
        if (filter.statuses() != null && !filter.statuses().isEmpty()) {
            var statusParams = JdbcEnumUtils.buildParams("status", filter.statuses());
            sql.append(" AND c.status IN ").append(statusParams);
        }
        sql.append(' ').append(SQL_ORDER);
        return sql.toString();
    }

    private MapSqlParameterSource buildParams(ReceivedChargesFilter filter) {
        var params = new MapSqlParameterSource()
                .addValue("payerId", filter.payerId().value());
        if (filter.statuses() != null && !filter.statuses().isEmpty()) {
            JdbcEnumUtils.addParams(params, "status", filter.statuses());
        }
        return params;
    }

    private RowMapper<ReceivedChargesItem> createMapper() {
        return (ResultSet rs, int rowNum) -> new ReceivedChargesItem(
                rs.getLong("id"),
                new ReceivedChargesItem.Issuer(
                        rs.getString("issuer_cpf"),
                        rs.getString("issuer_name")
                ),
                rs.getBigDecimal("amount"),
                rs.getString("description"),
                rs.getLong("created_at"),
                rs.getLong("due_at"),
                ChargeStatus.valueOf(rs.getString("status")),
                rs.getObject("paid_at") == null ? null : rs.getLong("paid_at")
        );
    }
}
