package com.danielpg.paymentgateway.domain.shared;

public final class DataMasking {

    private static final int MIN_LENGTH_TO_MASK = 5;

    private DataMasking() {}

    public static String maskName(String name) {
        return maskMiddle(name, 2);
    }

    public static String maskCpf(String cpf) {
        if (cpf == null) return "";
        var digits = cpf.replaceAll("\\D", "");
        return maskMiddle(digits, 2);
    }

    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) return "";
        var atIndex = email.indexOf('@');
        if (atIndex <= 0) return maskMiddle(email, 1);
        var local = email.substring(0, atIndex);
        var domain = email.substring(atIndex);
        return maskMiddle(local, 1) + domain;
    }

    private static String maskMiddle(String s, int visibleCharsPerSide) {
        if (s == null || s.isEmpty()) return "";
        var trimmed = s.trim();
        if (visibleCharsPerSide < 0) return "*".repeat(trimmed.length());

        var totalLength = trimmed.length();
        var visible = visibleCharsPerSide * 2;
        var hidden = totalLength - visible;

        if (hidden < MIN_LENGTH_TO_MASK) return "*".repeat(totalLength);

        var start = trimmed.substring(0, visibleCharsPerSide);
        var end = trimmed.substring(totalLength - visibleCharsPerSide);
        return start + "*".repeat(hidden) + end;
    }
}
