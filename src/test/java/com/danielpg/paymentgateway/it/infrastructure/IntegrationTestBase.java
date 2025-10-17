package com.danielpg.paymentgateway.it.infrastructure;

import com.danielpg.paymentgateway.domain.AppClock;
import com.danielpg.paymentgateway.domain.TimeMillis;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import static org.mockito.Mockito.when;

@SpringBootTest
public class IntegrationTestBase {

    @MockBean
    protected AppClock clock;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SpringLiquibase liquibase;

    @BeforeEach
    void beforeEach() throws Exception {
        when(clock.now()).thenReturn(TimeMillis.of(0L));
        resetDatabase();
    }

    private void resetDatabase() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DROP ALL OBJECTS;");
            }
        }
        liquibase.afterPropertiesSet();
    }
}
