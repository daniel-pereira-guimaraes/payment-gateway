package com.danielpg.paymentgateway.infrastructure.jdbc;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.Set;

public class JdbcEnumUtils {

    private JdbcEnumUtils() {
    }

    public static <E extends Enum<E>> String buildParams(String fieldName, Set<E> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        var sb = new StringBuilder("(");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb. append(':').append(fieldName).append(i);
        }
        return sb.append(')').toString();
    }

    public static <E extends Enum<E>> void addParams(MapSqlParameterSource params, String fieldName, Set<E> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        int i = 0;
        for (var value : values) {
            params.addValue(fieldName + i, value.name());
            i++;
        }
    }
}
