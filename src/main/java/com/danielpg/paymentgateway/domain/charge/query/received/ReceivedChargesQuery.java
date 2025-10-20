package com.danielpg.paymentgateway.domain.charge.query.received;

import java.util.List;

public interface ReceivedChargesQuery {
    List<ReceivedChargesItem> execute(ReceivedChargesFilter filter);
}
