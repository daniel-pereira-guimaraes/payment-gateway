package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.shared.AbstractMoney;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Optional;

public class Balance extends AbstractMoney {

    private static final BigDecimal MIN_VALUE = new BigDecimal("0.00");
    private static final BigDecimal MAX_VALUE = new BigDecimal("999999999.99");

    protected Balance(BigDecimal value) {
        super(value, MIN_VALUE, MAX_VALUE);
    }

    public Balance add(AbstractMoney addValue) {
        return new Balance(value().add(addValue.value()));
    }

    public Balance subtract(AbstractMoney subtractValue) {
        if (compareTo(subtractValue) < 0) {
            throw new InsufficientBalanceException();
        }
        return new Balance(value().subtract(subtractValue.value()));
    }

    public static Balance of(BigDecimal value) {
        return new Balance(value);
    }

    public static Optional<Balance> ofNullable(BigDecimal value) {
        return value == null
                ? Optional.empty()
                : Optional.of(new Balance(value));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }


}
