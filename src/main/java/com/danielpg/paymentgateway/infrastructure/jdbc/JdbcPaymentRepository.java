package com.danielpg.paymentgateway.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.TimeMillis;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.payment.Payment;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentId;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcPaymentRepository implements PaymentRepository {

    private static final String SQL_INSERT = """
            INSERT INTO tb_payment (charge_id, paid_at)
            VALUES (:chargeId, :paidAt)
            """;

    private static final String SQL_UPDATE = """
            UPDATE tb_payment SET paid_at = :paidAt
            WHERE id = :id
            """;

    private static final String SQL_SELECT_BY_ID =
            "SELECT id, charge_id, paid_at FROM tb_payment WHERE id = :id";

    private static final String SQL_EXISTS_BY_CHARGE_ID =
            "SELECT 1 FROM tb_payment WHERE charge_id = :chargeId";

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcPaymentRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean exists(ChargeId chargeId) {
        try {
            var params = Map.of("chargeId", chargeId.value());
            jdbc.queryForObject(SQL_EXISTS_BY_CHARGE_ID, params, Integer.class);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Optional<Payment> get(PaymentId id) {
        try {
            var params = Map.of("id", id.value());
            return Optional.ofNullable(jdbc.queryForObject(SQL_SELECT_BY_ID, params, (rs, rowNum) -> mapPayment(rs)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(Payment payment) {
        if (payment.id() == null) {
            var keyHolder = new CustomKeyHolder();
            jdbc.update(SQL_INSERT, commonParams(payment), keyHolder);
            payment.finalizeCreation(PaymentId.of(keyHolder.asLong()));
        } else {
            var params = commonParams(payment).addValue("id", payment.id().value());
            jdbc.update(SQL_UPDATE, params);
        }
    }

    private MapSqlParameterSource commonParams(Payment payment) {
        return new MapSqlParameterSource()
                .addValue("chargeId", payment.chargeId().value())
                .addValue("paidAt", payment.paidAt().value());
    }

    private Payment mapPayment(java.sql.ResultSet rs) throws java.sql.SQLException {
        return Payment.builder()
                .withId(PaymentId.of(rs.getLong("id")))
                .withChargeId(ChargeId.of(rs.getLong("charge_id")))
                .withPaidAt(TimeMillis.of(rs.getLong("paid_at")))
                .build();
    }
}
