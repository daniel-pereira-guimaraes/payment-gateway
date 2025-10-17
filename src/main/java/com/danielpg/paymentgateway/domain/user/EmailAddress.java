package com.danielpg.paymentgateway.domain.user;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class EmailAddress {

    private final String value;

    private EmailAddress(String value) {
        validate(value);
        this.value = value;
    }

    public static EmailAddress of(String address) {
        return new EmailAddress(address);
    }

    private void validate(String address) {
        InternetAddress internetAddress;
        try {
            internetAddress = new InternetAddress(address);
            internetAddress.validate();
        } catch (AddressException e) {
            throw new InvalidEmailAddressException(address, e);
        }
        ensureNoPersonalInfo(address, internetAddress);
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
