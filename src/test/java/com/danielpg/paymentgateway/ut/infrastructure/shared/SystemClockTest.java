package com.danielpg.paymentgateway.ut.infrastructure.shared;

import com.danielpg.paymentgateway.infrastructure.shared.SystemClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class SystemClockTest {

    private SystemClock clock;

    @BeforeEach
    void beforeEach() {
        clock = new SystemClock();
    }

    @Test
    void nowAlwaysReturnsNextTime() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            var t1 = clock.now();
            Thread.sleep(2L); //NOSONAR
            var t2 = clock.now();

            var elapsed = t2.value() - t1.value();
            assertThat(elapsed, greaterThanOrEqualTo(2L));
            assertThat(elapsed, lessThanOrEqualTo(50L)); // Tolerância
        }
    }

}
