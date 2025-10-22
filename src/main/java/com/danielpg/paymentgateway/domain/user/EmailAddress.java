package com.danielpg.paymentgateway.domain.user;

import io.micrometer.common.util.StringUtils;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;
import java.util.Optional;

public class EmailAddress {

    private final String value;

    private EmailAddress(String value) {
        this.value = validate(value);
    }

    public static EmailAddress of(String address) {
        return new EmailAddress(address);
    }

    public static Optional<EmailAddress> ofNullable(String address) {
        return StringUtils.isBlank(address)
                ? Optional.empty()
                : Optional.of(new EmailAddress(address));
    }

    private String validate(String address) {
        var trimmed = Objects.requireNonNull(address).trim();
        InternetAddress internetAddress;
        try {
            internetAddress = new InternetAddress(trimmed);
            internetAddress.validate();
        } catch (AddressException e) {
            throw new InvalidEmailAddressException(trimmed, e);
        }
        ensureNoPersonalInfo(trimmed, internetAddress);
        return trimmed;
    }

    private static void ensureNoPersonalInfo(
            String address, InternetAddress internetAddress) {
        if (internetAddress.getPersonal() != null) {
            throw new InvalidEmailAddressException(address);
        }
    }

    public String value() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof EmailAddress otherEmail
                && Objects.equals(value, otherEmail.value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
