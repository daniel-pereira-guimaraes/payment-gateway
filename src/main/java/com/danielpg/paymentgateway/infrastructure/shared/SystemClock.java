package com.danielpg.paymentgateway.infrastructure.shared;

import com.danielpg.paymentgateway.domain.shared.AppClock;
import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import org.springframework.stereotype.Component;

@Component
public class SystemClock implements AppClock {

    @Override
    public TimeMillis now() {
        return TimeMillis.now();
    }
}
