package com.danielpg.paymentgateway.it.infrastructure.jdbc;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.infrastructure.jdbc.JdbcEnumUtils;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class JdbcEnumUtilsTest {

    @Test
    void buildParamsReturnsMultipleSqlParams() {
        var statuses = Set.of(ChargeStatus.PAID, ChargeStatus.PENDING);
        var fieldName = "status";
        var expected = "(:status0,:status1)";

        var result = JdbcEnumUtils.buildParams(fieldName, statuses);

        assertThat(result, is(expected));
    }

    @Test
    void buildParamsReturnsSingleSqlParam() {
        var statuses = Set.of(ChargeStatus.CANCELED);
        var fieldName = "myField";
        var expected = "(:myField0)";

        var result = JdbcEnumUtils.buildParams(fieldName, statuses);

        assertThat(result, is(expected));
    }

    @Test
    void buildParamsReturnsEmptyStringWhenSetIsNull() {
        var result = JdbcEnumUtils.buildParams("status", null);

        assertThat(result, is(""));
    }

    @Test
    void buildParamsReturnsEmptyStringWhenSetIsEmpty() {
        var status = new HashSet<ChargeStatus>();

        var result = JdbcEnumUtils.buildParams("status", status);

        assertThat(result, is(""));
    }

    @Test
    void addParamsAddsAllValues() {
        var statuses = Set.of(ChargeStatus.PAID, ChargeStatus.CANCELED);
        var fieldName = "status";
        var params = new MapSqlParameterSource();

        JdbcEnumUtils.addParams(params, fieldName, statuses);

        assertThat(params.getValues().size(), is(2));
        assertThat(params.getValues().values(), containsInAnyOrder("PAID", "CANCELED"));
        assertThat(params.getValues().keySet(), containsInAnyOrder("status0", "status1"));
    }

    @Test
    void addParamsDoesNotModifyParamsWhenSetIsNull() {
        var params = new MapSqlParameterSource("initialKey", "initialValue");

        JdbcEnumUtils.addParams(params, "status", null);

        assertThat(params.getValues().size(), is(1));
        assertThat(params.getValue("initialKey"), is("initialValue"));
    }

    @Test
    void addParamsDoesNotModifyParamsWhenSetIsEmpty() {
        var params = new MapSqlParameterSource("initialKey", "initialValue");
        var status = new HashSet<ChargeStatus>();

        JdbcEnumUtils.addParams(params, "status", status);

        assertThat(params.getValues().size(), is(1));
        assertThat(params.getValue("initialKey"), is("initialValue"));
    }
}