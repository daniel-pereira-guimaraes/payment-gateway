package com.danielpg.paymentgateway.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.charge.payment.PaymentMethod;
import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.payment.Payment;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentId;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentRepository;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardCvv;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardExpirationDate;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardNumber;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcPaymentRepository implements PaymentRepository {

    private static final String SQL_INSERT = """
            INSERT INTO tb_payment (charge_id, paid_at, method, credit_card_number, credit_card_expiration, credit_card_cvv)
            VALUES (:chargeId, :paidAt, :method, :creditCardNumber, :creditCardExpiration, :creditCardCvv)
            """;

    private static final String SQL_UPDATE = """
            UPDATE tb_payment
            SET paid_at = :paidAt,
                method = :method,
                credit_card_number = :creditCardNumber,
                credit_card_expiration = :creditCardExpiration,
                credit_card_cvv = :creditCardCvv
            WHERE id = :id
            """;

    private static final String SQL_SELECT_BY_ID =
            "SELECT id, charge_id, paid_at, method, credit_card_number, credit_card_expiration, credit_card_cvv " +
                    "FROM tb_payment WHERE id = :id";

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
        var creditCard = payment.creditCard();
        return new MapSqlParameterSource()
                .addValue("chargeId", payment.chargeId().value())
                .addValue("paidAt", payment.paidAt().value())
                .addValue("method", payment.method().name())
                .addValue("creditCardNumber", creditCard != null ? creditCard.number().value() : null)
                .addValue("creditCardExpiration", creditCard != null ? creditCard.expirationDate().value() : null)
                .addValue("creditCardCvv", creditCard != null ? creditCard.cvv().value() : null);
    }

    private Payment mapPayment(java.sql.ResultSet rs) throws java.sql.SQLException {
        var method = PaymentMethod.valueOf(rs.getString("method"));
        var builder = Payment.builder()
                .withId(PaymentId.of(rs.getLong("id")))
                .withChargeId(ChargeId.of(rs.getLong("charge_id")))
                .withPaidAt(TimeMillis.of(rs.getLong("paid_at")))
                .withMethod(method);

        if (method == PaymentMethod.CREDIT_CARD) {
            mapCreditCard(rs, builder);
        }
        return builder.build();
    }

    private static void mapCreditCard(ResultSet rs, Payment.Builder builder) throws SQLException {
        builder.withCreditCard(
                CreditCard.builder()
                        .withNumber(CreditCardNumber.ofNullable(
                                rs.getString("credit_card_number")).orElse(null))
                        .withExpirationDate(CreditCardExpirationDate.ofNullable(
                                rs.getString("credit_card_expiration")).orElse(null))
                        .withCvv(CreditCardCvv.ofNullable(
                                rs.getString("credit_card_cvv")).orElse(null))
                        .build()
        );
    }
}
