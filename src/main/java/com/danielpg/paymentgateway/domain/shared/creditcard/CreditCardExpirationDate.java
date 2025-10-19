package com.danielpg.paymentgateway.domain.shared.creditcard;

import com.danielpg.paymentgateway.domain.shared.Validation;
import io.micrometer.common.util.StringUtils;

import java.time.YearMonth;
import java.util.Optional;

public class CreditCardExpirationDate {

    private static final String REQUIRED_MSG = "Data de expiração é requerida.";
    private static final String INVALID_MSG = "Data de expiração inválida.";
    private static final String SEP = "/";
    private static final int MAX_YEAR = 2999;
    private static final int MIN_MONTH = 1;
    private static final int MAX_MONTH = 12;

    private final String value;

    private CreditCardExpirationDate(String value) {
        this.value = validate(value);
    }

    public static CreditCardExpirationDate of(String value) {
        return new CreditCardExpirationDate(value);
    }

    public static Optional<CreditCardExpirationDate> ofNullable(String value) {
        return StringUtils.isBlank(value)
                ? Optional.empty()
                : Optional.of(new CreditCardExpirationDate(value));
    }

    public String value() {
        return value;
    }
    
    private String validate(String value) {
        Validation.required(value, REQUIRED_MSG);
        var trimmedValue = value.trim();

        var parts = trimmedValue.split(SEP);
        if (parts.length != 2) {
            throw new IllegalArgumentException(INVALID_MSG);
        }

        try {
            int month = Integer.parseInt(parts[0].trim());
            int year = parseYear(parts[1].trim());
            if (isExpiredOrInvalid(month, year)) {
                throw new IllegalArgumentException(INVALID_MSG);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(INVALID_MSG, e);
        }

        return trimmedValue;
    }

    private static int parseYear(String yearPart) {
        int len = yearPart.length();
        if (len == 2) {
            int century = (YearMonth.now().getYear() / 100) * 100;
            return century + Integer.parseInt(yearPart);
        }
        if (len == 4) {
            return Integer.parseInt(yearPart);
        }
        throw new IllegalArgumentException(INVALID_MSG);
    }

    private static boolean isExpiredOrInvalid(int month, int year) {
        if (month < MIN_MONTH || month > MAX_MONTH || year > MAX_YEAR) {
            return true;
        }
        return YearMonth.of(year, month).isBefore(YearMonth.now());
    }
}
