package com.danielpg.paymentgateway.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesQuery;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesFilter;
import com.danielpg.paymentgateway.domain.charge.query.issued.IssuedChargesItem;
import io.micrometer.common.util.StringUtils;
import liquibase.util.StringUtil;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class JdbcIssuedChargesQuery implements IssuedChargesQuery {

    private static final String SQL_BASE = """
            SELECT
                c.id,
                payer.cpf AS payer_cpf,
                payer.name AS payer_name,
                c.amount,
                c.description,
                c.created_at AS created_at,
                c.due_at,
                p.paid_at,
                c.status
            FROM tb_charge c
            JOIN tb_user payer ON c.payer_id = payer.id
            LEFT JOIN tb_payment p ON p.charge_id = c.id
            WHERE c.issuer_id = :issuerId
            """;

    private static final String SQL_ORDER = " ORDER BY c.id";

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<IssuedChargesItem> mapper;

    public JdbcIssuedChargesQuery(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.mapper = createMapper();
    }

    @Override
    public List<IssuedChargesItem> execute(IssuedChargesFilter filter) {
        var sql = buildSql(filter);
        var params = buildParams(filter);
        return jdbc.query(sql, params, mapper);
    }

    private String buildSql(IssuedChargesFilter filter) {
        var sql = new StringBuilder(SQL_BASE);
        var statusParams = JdbcEnumUtils.buildParams("status", filter.statuses());
        if (!StringUtils.isBlank(statusParams)) {
            sql.append(" AND c.status IN ").append(statusParams);
        }
        sql.append(' ').append(SQL_ORDER);
        return sql.toString();
    }

    private MapSqlParameterSource buildParams(IssuedChargesFilter filter) {
        var params = new MapSqlParameterSource()
                .addValue("issuerId", filter.issuerId().value());
        JdbcEnumUtils.addParams(params, "status", filter.statuses());
        return params;
    }

    private RowMapper<IssuedChargesItem> createMapper() {
        return (ResultSet rs, int rowNum) -> new IssuedChargesItem(
                rs.getLong("id"),
                new IssuedChargesItem.Payer(
                        rs.getString("payer_cpf"),
                        rs.getString("payer_name")
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
