package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.Validation;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.regex.Pattern;

public class PlainTextPassword {

    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 50;
    private static final String REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{%d,%d}$";
    private static final Pattern PATTERN =
            Pattern.compile(REGEX.formatted(MIN_LENGTH, MAX_LENGTH));

    public static final String INVALID_PASSWORD_MESSAGE =
            "A senha deve ter de %d a %d caracteres, com letras minúsculas, maiúsculas, números e outros símbolos.";

    private final String value;

    private PlainTextPassword(String value) {
        this.value = validate(value);
    }

    public static PlainTextPassword of(String value) {
        return new PlainTextPassword(value);
    }

    private static String validate(String plainText) {
        var trimmed = Validation.required(plainText, "A senha é requerida.");
        if (!PATTERN.matcher(trimmed).matches()) {
            throw new InvalidPasswordException(INVALID_PASSWORD_MESSAGE.formatted(MIN_LENGTH, MAX_LENGTH));
        }
        return trimmed;
    }

    public String value() {
        return value;
    }

    public HashedPassword toHashedPassword(PasswordHasher hasher) {
        return HashedPassword.of(hasher.hash(this));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
