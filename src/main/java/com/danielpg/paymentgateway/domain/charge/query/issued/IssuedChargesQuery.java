package com.danielpg.paymentgateway.domain.charge.query.issued;

import java.util.List;

public interface IssuedChargesQuery {
    List<IssuedChargesItem> execute(IssuedChargesFilter filter);
}
