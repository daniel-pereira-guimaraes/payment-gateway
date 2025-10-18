package com.danielpg.paymentgateway.domain.shared;

public final class DataMasking {

    private static final int MIN_LENGTH_TO_MASK = 5;

    private DataMasking() {}

    public static String maskName(String name) {
        return maskMiddle(name);
    }

    public static String maskCpf(String cpf) {
        if (cpf == null) return "";
        var digits = cpf.replaceAll("\\D", "");
        return maskMiddle(digits);
    }

    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) return "";
        var atIndex = email.indexOf('@');
        if (atIndex <= 0) return maskMiddle(email);
        var local = email.substring(0, atIndex);
        var domain = email.substring(atIndex);
        return maskMiddle(local) + domain;
    }

    private static String maskMiddle(String s) {
        if (s == null || s.isEmpty()) return "";
        if (s.length() < MIN_LENGTH_TO_MASK) return "*".repeat(s.length());
        return s.charAt(0) + "*".repeat(s.length() - 2) + s.charAt(s.length() - 1);
    }
}
